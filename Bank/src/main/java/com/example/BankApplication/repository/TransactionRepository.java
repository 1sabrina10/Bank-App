package com.example.BankApplication.repository;

import com.example.BankApplication.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository  extends JpaRepository<Transaction,String> {
}
