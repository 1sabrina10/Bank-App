package com.example.BankApplication.service;

import com.example.BankApplication.dto.EmailDetails;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);
     void sendEmailWithAttachement(EmailDetails emailDetails);
}
