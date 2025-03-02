package com.shizzy.moneytransfer.serviceimpl;

import com.shizzy.moneytransfer.enums.TransactionOperation;
import com.shizzy.moneytransfer.exception.FraudulentTransactionException;
import com.shizzy.moneytransfer.model.Country;
import com.shizzy.moneytransfer.model.FlaggedTransactionReason;
import com.shizzy.moneytransfer.model.Transaction;
import com.shizzy.moneytransfer.repository.CountryRepository;
import com.shizzy.moneytransfer.repository.FlaggedTransactionReasonRepository;
import com.shizzy.moneytransfer.repository.TransactionRepository;
import com.shizzy.moneytransfer.service.TransactionMonitoringService;
import com.shizzy.moneytransfer.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionMonitoringServiceImpl implements TransactionMonitoringService {

    private final TransactionRepository transactionRepository;
    private final CountryRepository countryRepository;
    private final WalletService walletService;
    private final FlaggedTransactionReasonRepository flaggedTransactionReasonRepository;

   @Override
    public void monitorTransaction(Transaction transaction) throws FraudulentTransactionException {

       List<FlaggedTransactionReason> reasons = new ArrayList<>();

       if (isRapidDepositWithdrawal(transaction.getWallet().getWalletId())) {
           reasons.add(
                   FlaggedTransactionReason.builder()
                           .reason("Rapid deposit and withdrawal detected.")
                           .transaction(transaction)
                           .build()
           );
       }

       if (isFrequentTransfers(transaction.getWallet().getWalletId())) {
           reasons.add(
                   FlaggedTransactionReason.builder()
                           .reason("Frequent transfers detected.")
                           .transaction(transaction)
                           .build()
           );
       }

       if (isUnusualAmount(transaction)) {
              reasons.add(
                     FlaggedTransactionReason.builder()
                            .reason("Unusual amount detected.")
                            .transaction(transaction)
                            .build()
              );
       }

       if (isDormantAccountSuddenlyActivated(transaction.getWallet().getWalletId())) {
              reasons.add(
                     FlaggedTransactionReason.builder()
                            .reason("Dormant account suddenly activated.")
                            .transaction(transaction)
                            .build()
              );
       }

       if (!reasons.isEmpty()) {
           flaggedTransactionReasonRepository.saveAll(reasons);
           flagTransaction(transaction, reasons);
       }

       if (reasons.size() > 2) {
           walletService.flagWallet(transaction.getWallet().getWalletId());
       }

    }

    private boolean isHighRiskCountry(Country country) {
        List<Country> highRiskCountries = countryRepository.getHighRiskCountries();
        return highRiskCountries.contains(country);
    }

    private boolean isRapidDepositWithdrawal(String walletId) {

        LocalDateTime recentDate = get24HoursAgoDate();

        // Fetch recent deposits and withdrawals within the last 24 hours
        List<Transaction> recentDeposits = transactionRepository.findRecentTransactions(walletId, TransactionOperation.DEPOSIT, recentDate);
        List<Transaction> recentWithdrawals = transactionRepository.findRecentTransactions(walletId, TransactionOperation.WITHDRAWAL, recentDate);

        // Combine both deposit and withdrawal transactions
        List<Transaction> recentTransactions = new ArrayList<>();
        recentTransactions.addAll(recentDeposits);
        recentTransactions.addAll(recentWithdrawals);

        // Check if there are more than 5 transactions (deposits or withdrawals) over $5,000 in the last 24 hours
        return recentTransactions.size() > 5 &&
                recentTransactions.stream().allMatch(t -> t.getAmount().compareTo(new BigDecimal("5000")) > 0);
    }

    private boolean isFrequentTransfers(String walletId) {
        List<Transaction> transactions = transactionRepository.findRecentTransactions(walletId, TransactionOperation.TRANSFER, get24HoursAgoDate());
        return transactions.size() > 10;
    }

    private boolean isUnusualAmount(Transaction transaction) {
       if(walletService.isWalletNew(transaction.getWallet().getWalletId())) {
           return false;
       }
        BigDecimal averageAmount = transactionRepository.getAverageTransactionAmount(transaction.getWallet().getWalletId());
        BigDecimal deviation = transaction.getAmount().subtract(averageAmount).abs();
        return deviation.compareTo(averageAmount.multiply(new BigDecimal("0.5"))) > 0;
    }

    private boolean isDormantAccountSuddenlyActivated(String walletId) {
        LocalDateTime lastActive = transactionRepository.findLastTransactionDate(walletId);

        // Check if the last activity was more than six months ago and if there have been more than 5 recent transactions
        return lastActive != null && lastActive.isBefore(getSixMonthsAgoDate())
                && transactionRepository.findRecentTransactions(walletId, TransactionOperation.DEPOSIT, get24HoursAgoDate()).size() > 5;
    }


    private void flagTransaction(Transaction transaction, List<FlaggedTransactionReason> reasons) throws  FraudulentTransactionException {
        transaction.setFlagged(true);
        transaction.setFlaggedTransactionReasons(reasons);
        transactionRepository.save(transaction);
        throw new FraudulentTransactionException("Operation failed. Please contact support for assistance.");
    }

    private LocalDateTime getSixMonthsAgoDate() {
        return LocalDateTime.now().minusMonths(6);
    }

    private LocalDateTime get24HoursAgoDate() {
        return LocalDateTime.now().minusHours(24);
    }
}
