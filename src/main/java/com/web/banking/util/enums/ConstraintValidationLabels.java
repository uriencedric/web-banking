package com.web.banking.util.enums;

import lombok.Getter;

@Getter
public class ConstraintValidationLabels {
  public static final String INVALID_IBAN = "Invalid IBAN supplied.";
  public static final String FIELD_NOT_NULL_IBAN = "IBAN field cannot be null.";
  public static final String FIELD_NOT_NULL_USER = "User field cannot be null.";
  public static final String FIELD_NOT_NULL_STATUS = "Status field cannot be null.";
}
