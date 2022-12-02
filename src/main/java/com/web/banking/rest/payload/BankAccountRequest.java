package com.web.banking.rest.payload;

import com.web.banking.util.enums.BankAccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountRequest {
  @NotNull private String accountNumber;
  @NotNull private String username;
  private List<BalanceRequest> balances;
  @NotNull private BankAccountStatus status;
}
