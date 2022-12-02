package com.web.banking.exception;

import com.web.banking.util.enums.ApplicationExceptionLabels;

public class ApplicationException extends Exception {
  public ApplicationException(String message) {
    super(message);
  }

  public ApplicationException(ApplicationExceptionLabels labels) {
    super(labels.getValue());
  }
}
