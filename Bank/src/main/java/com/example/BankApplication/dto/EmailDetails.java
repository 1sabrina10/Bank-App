package com.example.BankApplication.dto;

import lombok.Builder;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetails {
    private String recipient;
    private String messageBody ;
    private String subject;
    private String attachment;
}
