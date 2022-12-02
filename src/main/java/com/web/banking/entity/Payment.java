package com.web.banking.entity;

import com.web.banking.entity.validation.IBAN;
import com.web.banking.util.enums.ConstraintValidationLabels;
import com.web.banking.util.enums.Currencies;
import com.web.banking.util.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Builder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String _id;
  private double amount;
  private Currencies currency;

  @IBAN(message = ConstraintValidationLabels.INVALID_IBAN)
  private String beneficiaryAccountNumber;

  private String communication;
  private Date creationDate;
  private PaymentStatus status;

  @ManyToOne(
      targetEntity = BankAccount.class,
      cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE},
      fetch = FetchType.EAGER)
  private BankAccount giverAccount;

  @PrePersist
  public void prePersistCreationDate() {
    this.setCreationDate(new Date());
    this.set_id(UUID.randomUUID().toString());
  }
}
