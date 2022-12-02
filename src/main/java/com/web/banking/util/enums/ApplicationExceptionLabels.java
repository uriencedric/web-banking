package com.web.banking.util.enums;

public enum ApplicationExceptionLabels {
  INVALID_OPERATION("Your balance object is null, or does contain an empty array");

  private final String value;

  ApplicationExceptionLabels(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
