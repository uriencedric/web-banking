package com.web.banking.rest.payload;

import com.web.banking.util.enums.BalanceTypes;
import com.web.banking.util.enums.Currencies;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceRequest {
  private double amount;
  private Currencies currency;
  private BalanceTypes type;
}
