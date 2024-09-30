package com.example.BankApplication.utils;

import java.time.Year;

public class AccountUtils {
  public static final String ACCOUNT_EXISTS_CODE = "001";
  public static final String ACCOUNT_EXISTS_Message="user has an account created";

 public static final String ACCOUNT_NOT_EXISTS_CODE = "002";
 public static final String ACCOUNT_NOT_EXISTS_Message=" account does nt exists";

 public static final String ACCOUNT_FOUND_CODE = "003";
 public static final String ACCOUNT_FOUND_Message=" account found successfully";

public static final String ACCOUNT_CREATION_SUCCESS = "004";
public static final String ACCOUNT_CREATION_MESSAGE=" account  has been succefully created";

public static final String ACCOUNT_CREDITED_SUCCESS = "005";
public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE=" user account credited";

public static final String INSUFFICIENT_BALANCE_CODE = "006";
public static final String INSUFFICIENT_BALANCE_MESSAGE=" user has a inssufficient balance ";

public static final String DEBIT_BALANCE_SUCCESS_CODE = "007";
public static final String DEBIT_BALANCE_SUCCESS_MESSAGE=" debit successfully done ";

public static final String TRANSFER_SUCCESSFUL_CODE = "008";
public static final String TRANSFER_SUCCESSFUL_MESSAGE=" transfer successfully done ";

public static String generateAccountNumber(){
      //accountNumber
    /**
    2024+randomSixDigits*/
    Year currentYear = Year.now();
    int min = 100000;
    int max=999999;
    int randNumber = (int) Math.floor(Math.random() * (max- min + 1)+ min);
  String year = String.valueOf(currentYear);
  String randomNumber = String.valueOf(randNumber);
      return year + randomNumber;
  }


}
