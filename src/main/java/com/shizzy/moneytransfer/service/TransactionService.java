package com.shizzy.moneytransfer.service;

import com.shizzy.moneytransfer.api.ApiResponse;
import com.shizzy.moneytransfer.api.GenericResponse;
import com.shizzy.moneytransfer.dto.*;
import com.shizzy.moneytransfer.enums.TransactionOperation;
import com.shizzy.moneytransfer.enums.TransactionType;
import com.shizzy.moneytransfer.model.Transaction;
import com.shizzy.moneytransfer.model.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionService {
     ApiResponse<List<Transaction>> getAllTransactions(int page, int size);

     ApiResponse<List<Transaction>> getTransactionByReferenceNumber(String referenceNumber);

     ApiResponse<Transaction> getTransactionById(Integer id);

     ApiResponse<Transaction> updateTransaction(Integer id, TransactionStatusDTO status);

     ApiResponse<Page<Transaction>> getTransactionsByWallet(String walletId, int page, int size);

     ApiResponse<Page<Transaction>> getUserTransactionsByDate(TransactionsByDateRequest request);

     ApiResponse<?> searchTransactions(String inputString, String sortOrder, String searchQuery, int page, int size);

     ApiResponse<Transaction> updateTransactionStatus(String referenceNumber, UpdateTransactionRequest request);

     String getTransactionStatus(String referenceNumber);

     ApiResponse<TransactionFee> getTransactionFee(double amount);

     ApiResponse<Page<Transaction>> getTransactionsByFilter(Long walletId, String filter, String startDate, String endDate, int pageNumber, int pageSize);
}