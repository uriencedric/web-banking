package com.web.banking.util.enums;

public enum BusinessExceptionLabels {
  USER_NOT_FOUND("User not Found"),
  BAD_CREDENTIALS("Bad credentials"),
  INVALID_TOKEN("Invalid Token"),
  EXPIRED_TOKEN("Expired Token"),
  INVALID_HEADERS("Invalid headers. Authorization must start with 'Bearer '"),
  BANK_ACCOUNT_NOT_FOUND_FOR_USER("No bank account not found for user"),
  GIVER_BANK_ACCOUNT_NOT_BELONGING_TO_USER("Cannot proceed with current bank account"),
  PAYMENT_INVALID_ACTION("Cannot perform this action"),
  PAYMENT_NOT_FOUND("Payment not found"),
  APPLICATION_RELATED_ERROR("Cannot perform this action. Please contact our administrators"),
  ;

  private final String value;

  BusinessExceptionLabels(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
