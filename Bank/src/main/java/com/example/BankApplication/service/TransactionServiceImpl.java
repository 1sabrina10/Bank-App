package com.example.BankApplication.service;

import com.example.BankApplication.dto.TransactionDto;
import com.example.BankApplication.entity.Transaction;
import com.example.BankApplication.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    TransactionRepository transactionRepository;

    @Override
    public void saveTransaction(TransactionDto transactionDto) {
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDto.getTransactionType())
        .accountNumber(transactionDto.getAccountNumber())
        .amount(transactionDto.getAmount())
        .status("SUCCESS")
        .build();
transactionRepository.save(transaction);
System.out.println("transaction saved successfully");



    }
}