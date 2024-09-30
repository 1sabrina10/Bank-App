package com.example.BankApplication.dto;

import lombok.*;

import java.math.BigDecimal;
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountInfo {
    private String accountNumber;
    private String accountName;
    private BigDecimal accountBalance;

}
