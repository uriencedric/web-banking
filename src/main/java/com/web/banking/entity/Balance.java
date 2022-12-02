package com.web.banking.entity;

import com.web.banking.util.enums.BalanceTypes;
import com.web.banking.util.enums.Currencies;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Balance {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private double amount;
  private Currencies currency;
  private BalanceTypes type;
}
