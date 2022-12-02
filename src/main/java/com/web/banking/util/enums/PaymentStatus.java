package com.web.banking.util.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PaymentStatus {
  STATUS("executed");

  public String value;

  PaymentStatus(String value) {
    this.value = value;
  }
}
