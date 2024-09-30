package com.example.BankApplication.service;

import com.example.BankApplication.Config.JwtTokenProvider;
import com.example.BankApplication.dto.*;
import com.example.BankApplication.entity.Role;
import com.example.BankApplication.entity.User;
import com.example.BankApplication.repository.UserRepository;
import com.example.BankApplication.utils.AccountUtils;
import com.example.BankApplication.service.TransactionService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    UserRepository userRepository;
    @Autowired
    EmailService emailService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    PasswordEncoder passwordEncoder;
@Autowired
    AuthenticationManager authenticationManager;
@Autowired
    JwtTokenProvider jwtTokenProvider;
    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            // Logger pour un compte déjà existant
            logger.warn("Attempt to create account failed. Email already exists: {}", userRequest.getEmail());

            // Retourner la réponse indiquant que le compte existe déjà
            return BankResponse.builder()
                .codeResponse(AccountUtils.ACCOUNT_EXISTS_CODE)
                .messageResponse(AccountUtils.ACCOUNT_EXISTS_Message)
                .accountInfo(null)
                .build();
        }

        // Créer un nouvel utilisateur si le compte n'existe pas
        User user = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .role(Role.valueOf("ROLE_ADMIN".toString()))
                .build();

        // Sauvegarder l'utilisateur
        User savedUser = userRepository.save(user);

        // Logger pour la création réussie du compte
        logger.info("New account successfully created for email: {}", savedUser.getEmail());

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("congrats your account has been created \n Your Details:\n"+  "Account Name" + ""+ savedUser.getFirstName() + savedUser.getLastName() + savedUser.getOtherName() + savedUser.getAccountNumber())

                .build();
    emailService.sendEmailAlert(emailDetails);
        // Retourner la réponse de succès
        return BankResponse.builder()
                .codeResponse(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .messageResponse(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName())
                        .build())
                .build();
    }
    public BankResponse login(LoginDto loginDto){
        Authentication authentication= null;
        authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(),loginDto.getPassword())

        );
        EmailDetails loginAlert = EmailDetails.builder()
                .subject("You 're logged in")
                .recipient(loginDto.getEmail())
                .messageBody("Your logged into your account")
                .build();

        emailService.sendEmailAlert(loginAlert);

        return BankResponse.builder()
                .codeResponse("login successfully")
                .messageResponse(jwtTokenProvider.generateToken((Neo4jProperties.Authentication) authentication))
                .build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
       if(!isAccountExists){
           BankResponse.builder()
                   .codeResponse(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                   .messageResponse(AccountUtils.ACCOUNT_NOT_EXISTS_Message)
                   .accountInfo(null)
                   .build();
       }
      User foundUser =userRepository.findByAccountNumber(request.getAccountNumber());
        return BankResponse.builder()
                .codeResponse(AccountUtils.ACCOUNT_FOUND_CODE)
                .messageResponse(AccountUtils.ACCOUNT_FOUND_Message)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(foundUser.getAccountNumber())
                        .accountName(foundUser.getFirstName() +""+ foundUser.getLastName() +""+ foundUser.getOtherName())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {

         boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
       if(!isAccountExists){
         return AccountUtils.ACCOUNT_NOT_EXISTS_Message;
       }
       User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());

    return foundUser.getFirstName() +""+ foundUser.getLastName() +""+ foundUser.getOtherName();
}

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
            boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
       if(!isAccountExists){
           BankResponse.builder()
                   .codeResponse(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                   .messageResponse(AccountUtils.ACCOUNT_NOT_EXISTS_Message)
                   .accountInfo(null)
                   .build();
       }
       User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
         userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
         userRepository.save(userToCredit);
          TransactionDto transactionDto = TransactionDto.builder()
                 .accountNumber(userToCredit.getAccountNumber())
                 .transactionType("CREDIT")
                 .amount(request.getAmount())
                 .build();
        transactionService.saveTransaction( transactionDto);


         return BankResponse.builder()
                 .codeResponse(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                 .messageResponse(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                 .accountInfo(AccountInfo.builder()
                         .accountName(userToCredit.getFirstName() +""+ userToCredit.getLastName()+""+userToCredit.getOtherName())
                         .accountBalance(userToCredit.getAccountBalance())
                         .accountNumber(request.getAccountNumber())
                         .build())
                 .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
   boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
       if(!isAccountExists){
           BankResponse.builder()
                   .codeResponse(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                   .messageResponse(AccountUtils.ACCOUNT_NOT_EXISTS_Message)
                   .accountInfo(null)
                   .build();
       }
       User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());

    BigInteger availableBalance = userToDebit.getAccountBalance().toBigInteger();
    BigInteger debitAmount = request.getAmount().toBigInteger();
    if(availableBalance.intValue()<debitAmount.intValue()){
        return  BankResponse.builder()
                .codeResponse(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                .messageResponse(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                .accountInfo(null)
                .build();
    }
       else{
           userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
       userRepository.save(userToDebit);

        TransactionDto transactionDto = TransactionDto.builder()
                 .accountNumber(userToDebit.getAccountNumber())
                 .transactionType("DEBIT")
                 .amount(request.getAmount())
                 .build();
        transactionService.saveTransaction( transactionDto);

       return BankResponse.builder()
               .codeResponse(AccountUtils.DEBIT_BALANCE_SUCCESS_CODE)
               .messageResponse(AccountUtils.DEBIT_BALANCE_SUCCESS_MESSAGE)
               .accountInfo(AccountInfo.builder()
                       .accountNumber(request.getAccountNumber())
                       .accountName(userToDebit.getFirstName()+""+ userToDebit.getLastName()+""+userToDebit.getOtherName())
                       .accountBalance(userToDebit.getAccountBalance())
                       .build())
               .build();
       }
    }

    @Override

    public BankResponse transferRequest(Transfer request) {

        logger.info("Début du traitement du transfert pour le montant: {}", request.getAmount());

        boolean isDestinationAccountExists = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
        if (!isDestinationAccountExists) {
            logger.warn("Le compte destinataire {} n'existe pas.", request.getDestinationAccountNumber());
            return BankResponse.builder()
                .codeResponse(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                .messageResponse(AccountUtils.ACCOUNT_NOT_EXISTS_Message)
                .accountInfo(null)
                .build();
        }


        User sourceAccountUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());
        logger.info("Compte source récupéré: {} avec un solde de {}", request.getSourceAccountNumber(), sourceAccountUser.getAccountBalance());


        if (request.getAmount().compareTo(sourceAccountUser.getAccountBalance()) > 0) {
            logger.warn("Solde insuffisant pour le compte {}. Solde actuel: {}, Montant requis: {}",
                    request.getSourceAccountNumber(), sourceAccountUser.getAccountBalance(), request.getAmount());
            return BankResponse.builder()
                .codeResponse(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                .messageResponse(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                .accountInfo(null)
                .build();
        }


        logger.info("Débit de {} du compte source {}", request.getAmount(), request.getSourceAccountNumber());
        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmount()));
        userRepository.save(sourceAccountUser);
        logger.info("Nouveau solde du compte source {}: {}", request.getSourceAccountNumber(), sourceAccountUser.getAccountBalance());

        User destinationAccountUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber());
        logger.info("Compte destinataire récupéré: {}", request.getDestinationAccountNumber());

        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(request.getAmount()));
        userRepository.save(destinationAccountUser);
        logger.info("Crédit de {} sur le compte destinataire {}. Nouveau solde: {}", request.getAmount(), request.getDestinationAccountNumber(), destinationAccountUser.getAccountBalance());



        logger.info("Transfert réussi entre {} et {}", request.getSourceAccountNumber(), request.getDestinationAccountNumber());
         TransactionDto transactionDto = TransactionDto.builder()
                 .accountNumber(destinationAccountUser.getAccountNumber())
                 .transactionType("CREDIT")
                 .amount(request.getAmount())
                 .build();
        transactionService.saveTransaction( transactionDto);


        return BankResponse.builder()
            .codeResponse(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
            .messageResponse(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
            .accountInfo(null)
            .build();

    }


}





