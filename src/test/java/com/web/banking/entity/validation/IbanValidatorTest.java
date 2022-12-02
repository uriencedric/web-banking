package com.web.banking.entity.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class IbanValidatorTest {
  private IbanValidator ibanValidator;

  @BeforeEach
  void setup() {
    ibanValidator = new IbanValidator();
  }

  @Test
  void when_providing_wrong_iban_should_return_false() {
    String IBAN = "1259736";
    assertFalse(ibanValidator.isValid(IBAN, null));
  }

  @Test
  void when_providing_right_iban_should_return_true() {
    String IBAN = "LU970104876555648665";
    assertTrue(ibanValidator.isValid(IBAN, null));
  }
}
