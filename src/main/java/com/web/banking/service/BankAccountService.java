package com.web.banking.service;

import com.web.banking.entity.BankAccount;
import com.web.banking.rest.payload.BankAccountRequest;
import com.web.banking.rest.response.BankAccountResponse;

import java.util.List;

public interface BankAccountService {
  List<BankAccount> listBankAccountsForUser(String username);

  BankAccountResponse createBankAccount(BankAccountRequest bankAccountRequest);
}
