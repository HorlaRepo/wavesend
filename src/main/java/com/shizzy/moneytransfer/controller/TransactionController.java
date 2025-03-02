package com.shizzy.moneytransfer.controller;

import com.shizzy.moneytransfer.api.ApiResponse;
import com.shizzy.moneytransfer.api.GenericResponse;
import com.shizzy.moneytransfer.dto.WithdrawalRequestMapper;
import com.shizzy.moneytransfer.dto.*;
import com.shizzy.moneytransfer.model.Transaction;
import com.shizzy.moneytransfer.model.Wallet;
import com.shizzy.moneytransfer.service.TransactionService;
import com.shizzy.moneytransfer.serviceimpl.TransactionServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("transactions")
@RequiredArgsConstructor
@Validated
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ApiResponse<List<Transaction>> getAllTransactions(@RequestParam(defaultValue = "0", required = false) int page, @RequestParam(defaultValue = "50", required = false) int size){
        return transactionService.getAllTransactions(page,size);
    }

    @GetMapping("{id}")
    public ApiResponse<Transaction> getTransactionById(@PathVariable("id") Integer id) {
        return transactionService.getTransactionById(id);
    }

    @GetMapping("/wallet/{walletId}/sort")
    public ApiResponse<Page<Transaction>> getUserTransactionsBetweenDates(
            @PathVariable String walletId,
            @RequestParam(name = "startDate", required = true) String startDate,
            @RequestParam(name = "endDate", required = true) String endDate,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "8") int size) {

        System.out.println("Wallet ID: " + walletId);
        System.out.println("Start Date: " + startDate);
        System.out.println("End Date: " + endDate);

        TransactionsByDateRequest request = new TransactionsByDateRequest();
        request.setWalletId(walletId);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setPage(page);
        request.setSize(size);
        return transactionService.getUserTransactionsByDate(request);
    }

    @GetMapping("/wallet/{walletId}/filter")
    public ApiResponse<Page<Transaction>> getTransactionsByFilter(
            @PathVariable Long walletId,
            @RequestParam(name = "filter", required = true) String filter,
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "8") int size) {


        System.out.println("Filter: " + filter);
        System.out.println("Start Date: " + startDate);
        System.out.println("End Date: " + endDate);
        System.out.println("Page: " + page);
        System.out.println("Size: " + size);
        System.out.println("Wallet ID: " + walletId);

        return transactionService.getTransactionsByFilter(walletId, filter, startDate, endDate, page, size);
    }

    @GetMapping("/wallet/{walletId}")
    public ApiResponse<Page<Transaction>> getTransactionsByWallet(
            @PathVariable String walletId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return transactionService.getTransactionsByWallet(walletId, page, size);
    }


    @GetMapping("/search")
    public ApiResponse<?> searchTransactions(
            @RequestParam(required = false) String query,
            @RequestParam(required = false, defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return transactionService.searchTransactions(query, sortOrder, filter,page,size);
    }


    @PutMapping("/update/{id}")
    public ApiResponse<Transaction> updateTransaction(@PathVariable Integer id, @RequestBody TransactionStatusDTO status) {
        return transactionService.updateTransaction(id, status);
    }

    @GetMapping("/reference/{referenceNumber}")
    public ApiResponse<List<Transaction>> getTransactionByReferenceNumber(@PathVariable @Valid String referenceNumber){
        return transactionService.getTransactionByReferenceNumber(referenceNumber);
    }

    @PostMapping("/status/{referenceNumber}")
    public ApiResponse<Transaction> updateTransactionStatus(@PathVariable String referenceNumber, @Valid @RequestBody UpdateTransactionRequest request){
        return transactionService.updateTransactionStatus(referenceNumber,request);
    }

    @GetMapping("/transaction-fee")
    public ApiResponse<TransactionFee> getTransactionFee(@RequestParam double amount){
        return transactionService.getTransactionFee(amount);
    }

}
