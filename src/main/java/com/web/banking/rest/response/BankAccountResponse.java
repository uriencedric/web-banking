package com.web.banking.rest.response;

import com.web.banking.util.enums.BankAccountStatus;
import lombok.Data;

import java.util.List;

@Data
public class BankAccountResponse {
  private String accountNumber;
  private List<UserResponse> users;
  private List<BalanceResponse> balances;
  private BankAccountStatus status;
}
