package com.example.BankApplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transfer {
    private String sourceAccountNumber;
    private String destinationAccountNumber;
     private BigDecimal amount;
}
