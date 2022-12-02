package com.web.banking.rest.payload;

import com.web.banking.entity.validation.IBAN;
import com.web.banking.util.enums.ConstraintValidationLabels;
import com.web.banking.util.enums.Currencies;
import com.web.banking.util.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
  @NotNull private double amount;
  @NotNull private Currencies currency;

  @NotNull
  @IBAN(message = ConstraintValidationLabels.INVALID_IBAN)
  private String beneficiaryAccountNumber;

  private String communication;
  private PaymentStatus status;

  @NotNull
  @IBAN(message = ConstraintValidationLabels.INVALID_IBAN)
  private String giverAccount;
}
