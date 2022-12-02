package com.web.banking.service;

import com.web.banking.entity.BankAccount;
import com.web.banking.entity.Payment;
import com.web.banking.exception.ApplicationException;
import com.web.banking.rest.payload.DeletePaymentRequest;
import com.web.banking.rest.payload.PaymentRequest;

import java.util.List;

public interface PaymentService {

  Payment createSinglePayment(PaymentRequest paymentRequest);

  List<Payment> listPaymentsForUser(String username);

  List<Payment> listPaymentsForBeneficiary(String accountNumber);

  void deletePayment(DeletePaymentRequest payload);

  boolean isPaymentFromSameAccount(PaymentRequest paymentRequest);

  boolean isPaymentExceedAvailableBalance(
      PaymentRequest paymentRequest, BankAccount giverBankAccount) throws ApplicationException;

  boolean isAccountBlackListed(BankAccount bankAccount);
}
