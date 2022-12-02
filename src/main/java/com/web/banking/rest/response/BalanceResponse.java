package com.web.banking.rest.response;

import com.web.banking.util.enums.BalanceTypes;
import lombok.Data;

@Data
public class BalanceResponse {
  private double amount;
  private String currency;
  private BalanceTypes type;
}
