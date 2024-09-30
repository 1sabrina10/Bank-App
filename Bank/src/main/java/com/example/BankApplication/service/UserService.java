package com.example.BankApplication.service;

import com.example.BankApplication.dto.*;

public interface UserService {
  BankResponse createAccount(UserRequest userRequest);
  BankResponse balanceEnquiry(EnquiryRequest request);
  String nameEnquiry(EnquiryRequest request);
  BankResponse creditAccount(CreditDebitRequest request);
  BankResponse debitAccount(CreditDebitRequest request);
  BankResponse transferRequest (Transfer request);
  BankResponse login(LoginDto loginDto);
}
