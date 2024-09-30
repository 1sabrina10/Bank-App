package com.example.BankApplication.controller;

import com.example.BankApplication.entity.Transaction;
import com.example.BankApplication.service.BankStatment;
import com.itextpdf.text.DocumentException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Import pour logger
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/bankStatement")
@Slf4j // Lombok ajoute un Logger SLF4J
public class TransactionController {
    private final BankStatment bankStatment;

    @GetMapping
    public List<Transaction> generateStement(@RequestParam String accountNumber, @RequestParam String startDate, @RequestParam String endDate) throws DocumentException, FileNotFoundException {
        log.info("Received request to generate statement for account: {}, from: {}, to: {}", accountNumber, startDate, endDate);
        List<Transaction> transactions = bankStatment.generateStatement(accountNumber, startDate, endDate);
        log.info("Generated statement for account: {} with {} transactions", accountNumber, transactions.size());
        return transactions;
    }
}
