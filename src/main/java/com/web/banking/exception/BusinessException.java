package com.web.banking.exception;

import com.web.banking.util.enums.BusinessExceptionLabels;

public class BusinessException extends RuntimeException {
  public BusinessException(String message) {
    super(message);
  }

  public BusinessException(BusinessExceptionLabels labels) {
    super(labels.getValue());
  }
}
