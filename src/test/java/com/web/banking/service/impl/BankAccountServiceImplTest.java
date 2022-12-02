package com.web.banking.service.impl;

import com.web.banking.entity.Balance;
import com.web.banking.entity.BankAccount;
import com.web.banking.entity.User;
import com.web.banking.exception.BusinessException;
import com.web.banking.repository.BankAccountRepository;
import com.web.banking.repository.UserRepository;
import com.web.banking.rest.payload.BalanceRequest;
import com.web.banking.rest.payload.BankAccountRequest;
import com.web.banking.rest.response.BalanceResponse;
import com.web.banking.rest.response.BankAccountResponse;
import com.web.banking.rest.response.UserResponse;
import com.web.banking.util.enums.BalanceTypes;
import com.web.banking.util.enums.BankAccountStatus;
import com.web.banking.util.enums.Currencies;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceImplTest {

  @Mock private BankAccountRepository bankAccountRepository;
  @Mock private UserRepository userRepository;
  @Mock private ModelMapper modelMapper;
  @InjectMocks private BankAccountServiceImpl bankAccountService;

  @Test
  void when_list_bank_account_with_wrong_username_should_throw_ex() {
    String username = "user";
    List<BankAccount> bankAccountList = new ArrayList<>();
    User user = new User();
    user.setUsername(username);
    when(bankAccountRepository.findAllByUsers_Username(username))
        .thenReturn(java.util.Optional.of(bankAccountList));
    assertThrows(
        BusinessException.class, () -> bankAccountService.listBankAccountsForUser(username));
  }

  @Test
  void when_list_bank_account_with_right_username_should_return_list() {
    List<BankAccount> bankAccountList = new ArrayList<>();
    List<User> userList = new ArrayList<>();
    User user = new User();
    user.setUsername("user1");
    User user2 = new User();
    user.setUsername("user2");
    String IBAN = "NL59ABNA1388268736";

    userList.add(user);
    userList.add(user2);

    BankAccount bankAccount =
        BankAccount.builder()
            .id(1L)
            .accountNumber(IBAN)
            .status(BankAccountStatus.ENABLED)
            .balances(new ArrayList<>())
            .users(userList)
            .build();

    bankAccountList.add(bankAccount);
    when(bankAccountRepository.findAllByUsers_Username(user.getUsername()))
        .thenReturn(java.util.Optional.of(bankAccountList));
    assertEquals(1, bankAccountService.listBankAccountsForUser(user.getUsername()).size());
  }

  @Test
  void when_save_bank_account_with_right_payload_should_return_at_least_one_identical_field() {
    User user = new User();
    user.setUsername("username");
    String IBAN = "LU970104876555648665";
    BankAccount bankAccount =
        BankAccount.builder()
            .id(1L)
            .accountNumber(IBAN)
            .status(BankAccountStatus.ENABLED)
            .balances(new ArrayList<>())
            .users(Collections.singletonList(user))
            .build();

    BankAccountResponse bankAccountResponse = new BankAccountResponse();

    UserResponse userResponse = new UserResponse();
    userResponse.setUsername(user.getUsername());
    userResponse.setAddress(user.getAddress());

    bankAccountResponse.setUsers(Collections.singletonList(userResponse));
    bankAccountResponse.setStatus(BankAccountStatus.ENABLED);
    bankAccountResponse.setBalances(new ArrayList<>());
    bankAccountResponse.setAccountNumber(IBAN);

    BalanceRequest balanceRequest =
        BalanceRequest.builder()
            .amount(0.0)
            .currency(Currencies.EUR)
            .type(BalanceTypes.AVAILABLE)
            .build();

    BankAccountRequest payload =
        BankAccountRequest.builder()
            .accountNumber(IBAN)
            .balances(Collections.singletonList(balanceRequest))
            .status(BankAccountStatus.ENABLED)
            .username(user.getUsername())
            .build();

    when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
    when(bankAccountRepository.findByAccountNumber(IBAN)).thenReturn(Optional.of(bankAccount));
    when(modelMapper.map(payload.getBalances(), new TypeToken<List<Balance>>() {}.getType()))
        .thenReturn(Collections.singletonList(new BalanceResponse()));
    when(modelMapper.map(bankAccount, BankAccountResponse.class)).thenReturn(bankAccountResponse);
    when(bankAccountRepository.save(bankAccount)).thenReturn(bankAccount);

    assertEquals(bankAccountService.createBankAccount(payload).getAccountNumber(), IBAN);
    assertEquals(1, bankAccountService.createBankAccount(payload).getUsers().size());
  }

  @Test
  void when_save_bank_account_with_malformed_payload_should_throw_at_business_ex() {
    User user = new User();
    String IBAN = "NL59ABNA1388268736";
    BankAccountRequest payload =
        BankAccountRequest.builder()
            .accountNumber(IBAN)
            .balances(new ArrayList<>())
            .status(BankAccountStatus.ENABLED)
            .username(user.getUsername())
            .build();

    when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> bankAccountService.createBankAccount(payload));
  }
}
