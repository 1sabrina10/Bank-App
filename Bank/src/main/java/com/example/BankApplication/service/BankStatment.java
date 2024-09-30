package com.example.BankApplication.service;

import com.example.BankApplication.dto.EmailDetails;
import com.example.BankApplication.entity.Transaction;
import com.example.BankApplication.entity.User;
import com.example.BankApplication.repository.TransactionRepository;
import com.example.BankApplication.repository.UserRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Import pour le logger avec Lombok
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j // Lombok ajoute automatiquement un Logger SLF4J
public class BankStatment {
  @Autowired
    private final TransactionRepository transactionRepository;
   @Autowired
    private final UserRepository userRepository;
   @Autowired
    private EmailService emailService;
    private  static final String FILE="C:\\Users\\Click\\Desktop\\fintech\\MyStatement.pdf";

  public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate) throws FileNotFoundException, DocumentException {
    log.info("Generating bank statement for account: {}, startDate: {}, endDate: {}", accountNumber, startDate, endDate);

    /*try {*/
        // Trim the date strings to remove any leading or trailing spaces
        startDate = startDate.trim();
        endDate = endDate.trim();

        // Parsing the dates in ISO format (yyyy-MM-dd)
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

        // Filter transactions based on accountNumber and dates
        List<Transaction> transactionList = transactionRepository.findAll().stream()
            .filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
            .filter(transaction -> transaction.getCreatedAt() != null && !transaction.getCreatedAt().toLocalDate().isBefore(start))
            .filter(transaction -> transaction.getModifiedAt() != null && !transaction.getModifiedAt().toLocalDate().isAfter(end))
            .toList();
/*
        log.info("Successfully generated {} transactions", transactionList.size());

    } catch (DateTimeParseException e) {
        log.error("Error parsing date: startDate={}, endDate={}", startDate, endDate, e);
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format. Use ISO format (yyyy-MM-dd).");
    } catch (Exception e) {
        log.error("An unexpected error occurred while generating bank statement", e);
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to generate statement");
    }*/
    User user = userRepository.findByAccountNumber(accountNumber);
    String customerName = user.getFirstName() +" " + user.getLastName() +" "+ user.getOtherName();

    Rectangle statementSize = PageSize.A4;
    Document document = new Document(statementSize);
    log.info("Setting size of the document");
    OutputStream outputStream = new FileOutputStream(FILE);
    PdfWriter.getInstance(document, outputStream);
    document.open();

    PdfPTable bankInfoTable = new PdfPTable(1);
    PdfPCell bankName = new PdfPCell(new Phrase("Bank"));
    bankName.setBorder(0);
    bankName.setBackgroundColor(BaseColor.CYAN);
    bankName.setPadding(20f);

    PdfPCell bankAddress = new PdfPCell(new Phrase("neuilly sur seine"));
    bankAddress.setBorder(0);
    bankInfoTable.addCell(bankName);
    bankInfoTable.addCell(bankAddress);

    PdfPTable statementInfo = new PdfPTable(2);
    PdfPCell customerInfo = new PdfPCell(new Phrase("Start Date" +startDate));
    customerInfo.setBorder(0);
    PdfPCell statement = new PdfPCell(new Phrase("Statement Of Account"));
    statement.setBorder(0);
    PdfPCell stopDate = new PdfPCell(new Phrase("End of Date"+ endDate));
    stopDate.setBorder(0);
    PdfPCell name = new PdfPCell(new Phrase("Customer Name:"+customerName));
    name.setBorder(0);
    PdfPCell space = new PdfPCell();
    space.setBorder(0);
    PdfPCell address = new PdfPCell(new Phrase("Customer Adress"+user.getAddress()));
    address.setBorder(0);

    PdfPTable transactionsTable = new PdfPTable(4);
    PdfPCell date = new PdfPCell(new Phrase("DATE"));
    date.setBackgroundColor(BaseColor.CYAN);
    date.setBorder(0);

    PdfPCell transactionType = new PdfPCell(new Phrase("TRANSATION TYPE"));
    transactionType.setBackgroundColor(BaseColor.CYAN);
    transactionType.setBorder(0);
    PdfPCell transactionAmount = new PdfPCell(new Phrase("TRANSACTION AMOUNT"));
    transactionAmount.setBackgroundColor(BaseColor.CYAN);
    transactionAmount.setBorder(0);
    PdfPCell status = new PdfPCell(new Phrase("STATUS"));
    status.setBackgroundColor(BaseColor.CYAN);
    status.setBorder(0);

    transactionsTable.addCell(date);
    transactionsTable.addCell(transactionType);
    transactionsTable.addCell(transactionAmount);
    transactionsTable.addCell(status);

    transactionList.forEach(transaction -> {
                transactionsTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
                transactionsTable.addCell(new Phrase(transaction.getTransactionType()));
                transactionsTable.addCell(new Phrase(transaction.getAmount().toString()));
                transactionsTable.addCell(new Phrase(transaction.getStatus()));


            }
    );
    statementInfo.addCell(customerInfo);
    statementInfo.addCell(statement);
    statementInfo.addCell(endDate);
    statementInfo.addCell(name);
    statementInfo.addCell(space);
    statementInfo.addCell(address);

    document.add(bankInfoTable);
    document.add(statementInfo);
    document.add(transactionsTable);
    document.close();
   EmailDetails emailDetails= EmailDetails.builder()
            .recipient(user.getEmail())
            .subject("Statement Of Account")
            .messageBody("Statement Of Your Account")
            .attachment(FILE)

            .build();
 emailService.sendEmailWithAttachement(emailDetails);
     return transactionList;
}}





