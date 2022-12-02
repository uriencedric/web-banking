package com.web.banking.rest.controller;

import com.web.banking.entity.BankAccount;
import com.web.banking.rest.payload.BankAccountRequest;
import com.web.banking.rest.response.BankAccountResponse;
import com.web.banking.service.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class BankAccountController {

  private final BankAccountService bankAccountService;

  @Autowired
  public BankAccountController(BankAccountService bankAccountService) {
    this.bankAccountService = bankAccountService;
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<BankAccount> bankAccounts() {
    return this.bankAccountService.listBankAccountsForUser("john.doe");
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public BankAccountResponse create(@RequestBody @Valid BankAccountRequest payload) {
    return this.bankAccountService.createBankAccount(payload);
  }
}
