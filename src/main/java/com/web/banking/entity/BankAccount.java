package com.web.banking.entity;

import com.web.banking.entity.validation.IBAN;
import com.web.banking.util.enums.BankAccountStatus;
import com.web.banking.util.enums.ConstraintValidationLabels;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @NotNull(message = ConstraintValidationLabels.FIELD_NOT_NULL_IBAN)
  @IBAN(message = ConstraintValidationLabels.INVALID_IBAN)
  private String accountNumber;

  @NotNull(message = ConstraintValidationLabels.FIELD_NOT_NULL_USER)
  @ManyToMany(targetEntity = User.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<User> users = new ArrayList<>();

  @ManyToMany(targetEntity = Balance.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Balance> balances = new ArrayList<>();

  @NotNull(message = ConstraintValidationLabels.FIELD_NOT_NULL_STATUS)
  private BankAccountStatus status;
}
