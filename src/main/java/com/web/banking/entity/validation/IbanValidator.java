package com.web.banking.entity.validation;

import org.apache.commons.validator.routines.checkdigit.IBANCheckDigit;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IbanValidator implements ConstraintValidator<IBAN, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    IBANCheckDigit ibanCheckDigit = new IBANCheckDigit();
    return ibanCheckDigit.isValid(value);
  }
}
