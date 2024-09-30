package com.example.BankApplication.controller;

import com.example.BankApplication.dto.*;
import com.example.BankApplication.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "user Account")
public class UserController {

    // Initialiser le logger
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @PostMapping
    public BankResponse createAccount(@RequestBody UserRequest userRequest) {
        // Log d'entrée dans la méthode
        logger.info("Received request to create account for email: {}", userRequest.getEmail());

        BankResponse response = userService.createAccount(userRequest);

        // Log de succès ou d'échec en fonction de la réponse
        if (response.getCodeResponse().equals("SUCCESS_CODE")) {
            logger.info("Account successfully created for email: {}", userRequest.getEmail());
        } else {
            logger.warn("Failed to create account for email: {} - Reason: {}", userRequest.getEmail(), response.getMessageResponse());
        }

        return response;
    }
    @PostMapping("/login")
    public BankResponse login(@RequestBody LoginDto loginDto){
        return userService.login(loginDto);
    }
      @GetMapping("balanceEnquiry")
    public  BankResponse balanceEnquiry(@RequestBody EnquiryRequest request){
       return  userService.balanceEnquiry(request);
    }
 @GetMapping("nameEnquiry")
    public  String nameEnquiry(@RequestBody EnquiryRequest request){
       return  userService.nameEnquiry(request);
    }
    @PostMapping("creditAccount")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request){
       return userService.creditAccount(request);
    }

@PostMapping("debitAccount")
        BankResponse debitAccount(@RequestBody CreditDebitRequest request){

        return userService.debitAccount(request);
}

@PostMapping("transfer")
BankResponse transferRequest(@RequestBody Transfer request){
      return  userService.transferRequest(request);
}
}


