package com.web.banking.util.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BalanceTypes {
  END_OF_DAY("end_of_day"),
  AVAILABLE("available");

  private final String value;

  BalanceTypes(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
