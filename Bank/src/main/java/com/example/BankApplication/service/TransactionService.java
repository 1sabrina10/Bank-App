package com.example.BankApplication.service;

import com.example.BankApplication.dto.TransactionDto;


public interface TransactionService {
    void saveTransaction(TransactionDto transactionDto);
}
