package com.shizzy.moneytransfer.serviceimpl;

import com.shizzy.moneytransfer.api.ApiResponse;
import com.shizzy.moneytransfer.api.GenericResponse;
import com.shizzy.moneytransfer.enums.TransactionOperation;
import com.shizzy.moneytransfer.service.TransactionService;
import com.shizzy.moneytransfer.dto.WithdrawalRequestMapper;
import com.shizzy.moneytransfer.dto.*;
import com.shizzy.moneytransfer.enums.TransactionType;
import com.shizzy.moneytransfer.exception.*;
import com.shizzy.moneytransfer.exception.IllegalArgumentException;
import com.shizzy.moneytransfer.model.*;
import com.shizzy.moneytransfer.repository.*;
import com.shizzy.moneytransfer.service.PaymentService;
import com.shizzy.moneytransfer.service.WalletService;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.shizzy.moneytransfer.util.CacheNames.*;
import static com.shizzy.moneytransfer.util.TransactionSpecification.buildSpecification;


@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AdminRepository adminRepository;
    private final CountryRepository countryRepository;
    private final TransactionStatusRepository statusRepository;
    private final WalletRepository walletRepository;


    @Override
    //@Cacheable(value = TRANSACTIONS)
    public ApiResponse<List<Transaction>> getAllTransactions(int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber,pageSize, Sort.by(Sort.Direction.ASC, "transactionId"));
        List<Transaction> transactions = transactionRepository.findAll(pageRequest).getContent();
        if(transactions.isEmpty())
            throw new ResourceNotFoundException("No transactions found");

        return ApiResponse.<List<Transaction>>builder()
                .success(true)
                .message(transactions.size() + " transactions found")
                .data(transactions)
                .build();
    }

    @Override
    public ApiResponse<List<Transaction>> getTransactionByReferenceNumber(@NotBlank String referenceNumber) {
        List<Transaction> transactions = transactionRepository.findTransactionByReferenceNumber(referenceNumber);
        if(transactions.isEmpty())
            throw new ResourceNotFoundException("No transactions found");

        return ApiResponse.<List<Transaction>>builder()
                .success(true)
                .message(transactions.size() + " transactions found")
                .data(transactions)
                .build();
    }

    @Override
    @CachePut(value = SINGLE_TRANSACTION, key = TRANSACTION_KEY)
    public ApiResponse<Transaction> getTransactionById(Integer transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(()-> new ResourceNotFoundException("transaction not found"));

        return ApiResponse.<Transaction>builder()
                .success(true)
                .message("Transaction found")
                .data(transaction)
                .build();
    }

    @Override
    @Cacheable(value = SEARCH_RESULT)
    public ApiResponse<List<Transaction>> searchTransactions(String searchQuery, String sortOrder, String searchFilter, int page, int size) {
        Specification<Transaction> specification = buildSpecification(searchQuery, searchFilter);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder), "transactionDate"));
        Page<Transaction> transactions = transactionRepository.findAll(specification, pageable);

        return ApiResponse.<List<Transaction>>builder()
                .success(true)
                .message(transactions.getTotalElements()+" Transactions found")
                .data(transactions.getContent())
                .build();
    }

    @Override
    public ApiResponse<Transaction> updateTransactionStatus(String referenceNumber, UpdateTransactionRequest request) {
        Transaction transaction = transactionRepository.findTransactionByReferenceNumber(referenceNumber).get(0);
        transaction.setCurrentStatus(request.getStatus());
        transactionRepository.save(transaction);
        return ApiResponse.<Transaction>builder()
                .success(true)
                .message("Transaction updated successfully")
                .data(transaction)
                .build();
    }

    @Override
    public String getTransactionStatus(String referenceNumber) {
        Transaction transaction = transactionRepository.findTransactionByReferenceNumber(referenceNumber).get(0);
        return transaction.getCurrentStatus();
    }

    @Override
    public ApiResponse<TransactionFee> getTransactionFee(double amount) {
        BigDecimal fees = BigDecimal.valueOf(amount).multiply(BigDecimal.valueOf(0.02)).setScale(2, RoundingMode.HALF_UP);
        if (fees.compareTo(BigDecimal.valueOf(100)) > 0) {
            fees = BigDecimal.valueOf(100);
        }
        TransactionFee transactionFee = new TransactionFee();
        transactionFee.setFee(fees.doubleValue());
        return ApiResponse.<TransactionFee>builder()
                .success(true)
                .message("Transaction fee calculated successfully")
                .data(transactionFee)
                .build();
    }

    @Override
    public ApiResponse<Page<Transaction>> getTransactionsByFilter(
            Long walletId,
            String filter,
            String startDate,
            String endDate,
            int pageNumber,
            int pageSize) {
        Page<Transaction> transactions;
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "transactionId"));

        Optional<LocalDate> startingDate = Optional.ofNullable(startDate).map(LocalDate::parse);
        Optional<LocalDate> endingDate = Optional.ofNullable(endDate).map(LocalDate::parse);

        if (startingDate.isPresent() && endingDate.isPresent()) {
            LocalDateTime startDateTime = startingDate.get().atStartOfDay();
            LocalDateTime endDateTime = endingDate.get().atTime(23, 59, 59);
            transactions = switch (filter.toUpperCase()) {
                case "PAYMENTS_SENT" ->
                        transactionRepository.findByWalletIdAndOperationAndTransactionTypeAndTransactionDateBetween(walletId, TransactionOperation.TRANSFER, TransactionType.DEBIT, startDateTime, endDateTime, pageRequest);
                case "PAYMENTS_RECEIVED" ->
                        transactionRepository.findByWalletIdAndOperationAndTransactionTypeAndTransactionDateBetween(walletId, TransactionOperation.TRANSFER, TransactionType.CREDIT, startDateTime, endDateTime, pageRequest);
                case "REFUNDS" -> transactionRepository.findByWalletIdAndOperationAndTransactionDateBetween(walletId, TransactionOperation.REVERSAL, startDateTime, endDateTime, pageRequest);
                case "WITHDRAWAL" -> transactionRepository.findByWalletIdAndOperationAndTransactionDateBetween(walletId, TransactionOperation.WITHDRAWAL, startDateTime, endDateTime, pageRequest);
                case "DEPOSIT" -> transactionRepository.findByWalletIdAndOperationAndTransactionDateBetween(walletId, TransactionOperation.DEPOSIT, startDateTime, endDateTime, pageRequest);
                default -> throw new IllegalArgumentException("Invalid filter: " + filter);
            };
        } else {
            transactions = switch (filter.toUpperCase()) {
                case "PAYMENTS_SENT" ->
                        transactionRepository.findByWalletIdAndOperationAndTransactionType(walletId, TransactionOperation.TRANSFER, TransactionType.DEBIT, pageRequest);
                case "PAYMENTS_RECEIVED" ->
                        transactionRepository.findByWalletIdAndOperationAndTransactionType(walletId, TransactionOperation.TRANSFER, TransactionType.CREDIT, pageRequest);
                case "REFUNDS" -> transactionRepository.findByWalletIdAndOperation(walletId, TransactionOperation.REVERSAL, pageRequest);
                case "WITHDRAWAL" -> transactionRepository.findByWalletIdAndOperation(walletId, TransactionOperation.WITHDRAWAL, pageRequest);
                case "DEPOSIT" -> transactionRepository.findByWalletIdAndOperation(walletId, TransactionOperation.DEPOSIT, pageRequest);
                default -> throw new IllegalArgumentException("Invalid filter: " + filter);
            };
        }

        return ApiResponse.<Page<Transaction>>builder()
                .success(true)
                .message(transactions.getTotalElements() + " transactions found")
                .data(transactions)
                .build();
    }


    @Override
    @CacheEvict(value = TRANSACTIONS, key = TRANSACTION_KEY)
    public ApiResponse<Transaction> updateTransaction(Integer id, TransactionStatusDTO statusDTO) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("transaction not found"));

        TransactionStatus status = TransactionStatus.builder()
                .status(statusDTO.status())
                .note(statusDTO.note())
                .statusDate(getCurrentDate())
                .admin(getCurrentAdmin())
                .transaction(transaction)
                .build();
        statusRepository.save(status);

        transactionRepository.save(transaction);

        return ApiResponse.<Transaction>builder()
                .success(true)
                .message("Transaction updated successfully")
                .data(transaction)
                .build();
    }

    @Override
    public ApiResponse<Page<Transaction>> getTransactionsByWallet(String walletId, int page, int size) {
        Wallet wallet = walletRepository.findWalletByWalletId(walletId)
                .orElseThrow(()-> new ResourceNotFoundException("Wallet not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "transactionId"));
        Page<Transaction> transactions = transactionRepository.findTransactionsByWallet(wallet, pageable);

        return ApiResponse.<Page<Transaction>>builder()
                .success(true)
                .message(transactions.getTotalElements() + " transactions found")
                .data(transactions)
                .build();
    }


    @Override
    public ApiResponse<Page<Transaction>> getUserTransactionsByDate(TransactionsByDateRequest request) {

        Wallet wallet = walletRepository.findWalletByWalletId(request.getWalletId())
                .orElseThrow(()-> new ResourceNotFoundException("Wallet not found"));

        LocalDateTime startDate = LocalDate.parse(request.getStartDate()).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(request.getEndDate()).atStartOfDay();

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by(Sort.Direction.DESC, "transactionId"));

        Page<Transaction> transactions = transactionRepository.findTransactionsByWalletIdAndDateRange(wallet, startDate, endDate, pageable);

        return ApiResponse.<Page<Transaction>>builder()
                .success(true)
                .message(transactions.getTotalElements() + " transactions found")
                .data(transactions)
                .build();
    }


    private @NotNull String generateMTCN() {
        SecureRandom random = new SecureRandom();
        String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder mtcn = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            mtcn.append(characters.charAt(random.nextInt(characters.length())));
            if ((mtcn.length() + 1) % 4 == 0 && mtcn.length() < 14) {
                mtcn.append("-");
            }
        }
        return mtcn.toString();
    }

    private Admin getCurrentAdmin() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetail = (UserDetails) authentication.getPrincipal();
        String usernameFromAccessToken = userDetail.getUsername();

        Optional<Admin> admin = adminRepository.findAdminByUsername(usernameFromAccessToken);

        return admin.orElseThrow(()-> new ResourceNotFoundException("No Admin Found"));
    }

    private Country getCountry(String countryName) {
        return
                countryRepository.getCountryByName(countryName)
                        .orElseThrow(()-> new ResourceNotFoundException("Country not found"));
    }


    private LocalDateTime getCurrentDate() {
        return LocalDateTime.now();
    }

    public Transaction createTransaction(
            @NotNull Wallet wallet,
            @NotNull CreateTransactionRequestBody requestBody,
            TransactionType transactionType,
            String description,
            String referenceNumber) {
        return Transaction.builder()
                .wallet(wallet)
                .amount(requestBody.amount())
                .transactionType(transactionType)
                .narration(requestBody.narration())
                .description(description)
                .currentStatus(com.shizzy.moneytransfer.enums.TransactionStatus.PENDING.getValue())
                .referenceNumber(referenceNumber)
                .operation(TransactionOperation.TRANSFER)
                .transactionDate(LocalDateTime.now())
                .build();
    }
}
