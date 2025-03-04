package com.shizzy.moneytransfer.serviceimpl;

import com.shizzy.moneytransfer.api.ApiResponse;
import com.shizzy.moneytransfer.api.GenericResponse;
import com.shizzy.moneytransfer.service.BankAccountService;
import com.shizzy.moneytransfer.dto.AddBankAccountRequest;
import com.shizzy.moneytransfer.dto.AddBeneficiaryRequest;
import com.shizzy.moneytransfer.exception.ResourceNotFoundException;
import com.shizzy.moneytransfer.model.BankAccount;
import com.shizzy.moneytransfer.dto.Beneficiary;
import com.shizzy.moneytransfer.repository.BankAccountRepository;
import com.shizzy.moneytransfer.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;

//    @Autowired
//    public void setPaymentService( @Qualifier("flutterwaveServiceImpl") PaymentService paymentService) {
//        this.paymentService = paymentService;
//    }


    @Override
    public ApiResponse<String> deleteBankAccount(Integer bankAccountId) {
        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(()-> new ResourceNotFoundException("Bank Account not found"));

        bankAccountRepository.delete(bankAccount);

        return ApiResponse.<String>builder()
                .data("Bank Account deleted successfully")
                .message("Bank Account deleted successfully")
                .success(true)
                .build();
    }

    @Transactional
    @Override
    public ApiResponse<BankAccount> addBankAccount(Authentication connectedUser, AddBankAccountRequest addBankAccountRequest) {

        String region = addBankAccountRequest.getRegion();
        Map<String, Object> bankAccountDetails = addBankAccountRequest.getBankAccountDetails();

        BankAccount bankAccount = BankAccount.builder()
                .region(region)
                .accountNumber(bankAccountDetails.get("accountNumber").toString())
                .accountType(bankAccountDetails.get("accountType").toString())
                .bankName(bankAccountDetails.get("bankName").toString())
                .currency(bankAccountDetails.get("currency").toString())
                .bankCountry(bankAccountDetails.get("bankCountry").toString())
                .createdBy(connectedUser.getName())
                .build();

        switch (region) {
            case "africa":
                bankAccount.setAccountName(bankAccountDetails.get("accountName").toString());
                bankAccount.setBankCode((String) bankAccountDetails.get("ifscCode"));
                bankAccount.setPaymentMethod((String) bankAccountDetails.get("paymentMethod"));
                break;
            case "eu":
                bankAccount.setRoutingNumber((String) bankAccountDetails.get("routingNumber"));
                bankAccount.setSwiftCode((String) bankAccountDetails.get("swiftCode"));
                bankAccount.setBeneficiaryName((String) bankAccountDetails.get("beneficiaryName"));
                bankAccount.setBeneficiaryAddress((String) bankAccountDetails.get("beneficiaryAddress"));
                bankAccount.setBeneficiaryCountry((String) bankAccountDetails.get("beneficiaryCountry"));
                bankAccount.setPostalCode((String) bankAccountDetails.get("postalCode"));
                bankAccount.setStreetNumber((String) bankAccountDetails.get("streetNumber"));
                bankAccount.setStreetName((String) bankAccountDetails.get("streetName"));
                bankAccount.setCity((String) bankAccountDetails.get("city"));
                break;
            case "us":
                bankAccount.setRoutingNumber((String) bankAccountDetails.get("routingNumber"));
                bankAccount.setSwiftCode((String) bankAccountDetails.get("swiftCode"));
                bankAccount.setBeneficiaryName((String) bankAccountDetails.get("beneficiaryName"));
                bankAccount.setBeneficiaryAddress((String) bankAccountDetails.get("beneficiaryAddress"));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + region);
        }

        if (bankAccountRepository.findByAccountNumber(bankAccountDetails.get("accountNumber").toString()).isPresent()){
            return ApiResponse.<BankAccount>builder()
                    .data(null)
                    .message("Bank Account already exists")
                    .success(false)
                    .build();
        }

        bankAccountRepository.save(bankAccount);

        return ApiResponse.<BankAccount>builder()
                .data(bankAccount)
                .message("Bank Account added successfully")
                .success(true)
                .build();
    }

    @Override
    public ApiResponse<List<BankAccount>> getBankAccountsByUserId(Authentication connectedUser) {

        List<BankAccount> bankAccounts = bankAccountRepository.findBankAccountByCreatedBy(connectedUser.getName());

        if (bankAccounts.isEmpty()){
            return ApiResponse.<List<BankAccount>>builder()
                    .data(null)
                    .message("No Bank Account found")
                    .success(false)
                    .build();
        }

        return ApiResponse.<List<BankAccount>>builder()
                .data(bankAccounts)
                .message("Bank Accounts retrieved successfully")
                .success(true)
                .build();
    }

    @Override
    public ApiResponse<BankAccount> getBankAccountByAccountNumber(String accountNumber) {
        BankAccount bankAccount = bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new ResourceNotFoundException("Bank Account not found"));

        return ApiResponse.<BankAccount>builder()
                .data(bankAccount)
                .message("Bank Account retrieved successfully")
                .success(true)
                .build();
    }

    @Override
    public ApiResponse<Long> getBankAccountCountByUserId(Authentication connectedUser) {
        long count = bankAccountRepository.countBankAccountByCreatedBy(connectedUser.getName());

        return ApiResponse.<Long>builder()
                .data(count)
                .message("Bank Account count retrieved successfully")
                .success(true)
                .build();
    }
}
