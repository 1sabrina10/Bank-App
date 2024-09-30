package com.example.BankApplication.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BankResponse {
    private String codeResponse;
    private String messageResponse;
    private AccountInfo accountInfo;

}
