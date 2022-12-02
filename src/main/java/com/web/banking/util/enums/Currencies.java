package com.web.banking.util.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Currencies {
  EUR("EUR");

  private final String value;

  Currencies(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
