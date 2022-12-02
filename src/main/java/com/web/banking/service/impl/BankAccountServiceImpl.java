package com.web.banking.service.impl;

import com.web.banking.entity.Balance;
import com.web.banking.entity.BankAccount;
import com.web.banking.entity.User;
import com.web.banking.exception.BusinessException;
import com.web.banking.repository.BankAccountRepository;
import com.web.banking.repository.UserRepository;
import com.web.banking.rest.payload.BankAccountRequest;
import com.web.banking.rest.response.BankAccountResponse;
import com.web.banking.service.BankAccountService;
import com.web.banking.util.enums.BusinessExceptionLabels;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

@Service
public class BankAccountServiceImpl implements BankAccountService {

  private final BankAccountRepository bankAccountRepository;
  private final UserRepository userRepository;
  private final ModelMapper modelMapper;

  @Autowired
  public BankAccountServiceImpl(
      BankAccountRepository bankAccountRepository,
      UserRepository userRepository,
      ModelMapper modelMapper) {
    this.bankAccountRepository = bankAccountRepository;
    this.userRepository = userRepository;
    this.modelMapper = modelMapper;
  }

  @Override
  public List<BankAccount> listBankAccountsForUser(String username) {
    return bankAccountRepository
        .findAllByUsers_Username(username)
        .filter(bankAccounts -> !bankAccounts.isEmpty())
        .orElseThrow(
            () -> new BusinessException(BusinessExceptionLabels.BANK_ACCOUNT_NOT_FOUND_FOR_USER));
  }

  @Override
  public BankAccountResponse createBankAccount(BankAccountRequest bankAccountRequest) {
    Type balanceType = new TypeToken<List<Balance>>() {}.getType();
    User user =
        userRepository
            .findByUsername(bankAccountRequest.getUsername())
            .orElseThrow(() -> new BusinessException(BusinessExceptionLabels.USER_NOT_FOUND));

    BankAccount bankAccount =
        bankAccountRepository
            .findByAccountNumber(bankAccountRequest.getAccountNumber())
            .orElse(
                BankAccount.builder()
                    .status(bankAccountRequest.getStatus())
                    .accountNumber(bankAccountRequest.getAccountNumber())
                    .users(Collections.singletonList(user))
                    .balances(modelMapper.map(bankAccountRequest.getBalances(), balanceType))
                    .build());

    return modelMapper.map(bankAccountRepository.save(bankAccount), BankAccountResponse.class);
  }
}
