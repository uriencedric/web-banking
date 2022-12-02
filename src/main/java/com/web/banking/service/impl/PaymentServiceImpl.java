package com.web.banking.service.impl;

import com.web.banking.entity.Balance;
import com.web.banking.entity.BankAccount;
import com.web.banking.entity.Payment;
import com.web.banking.exception.ApplicationException;
import com.web.banking.exception.BusinessException;
import com.web.banking.repository.BankAccountRepository;
import com.web.banking.repository.PaymentRepository;
import com.web.banking.rest.payload.DeletePaymentRequest;
import com.web.banking.rest.payload.PaymentRequest;
import com.web.banking.service.PaymentService;
import com.web.banking.service.UserService;
import com.web.banking.util.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

  private final PaymentRepository paymentRepository;
  private final BankAccountRepository bankAccountRepository;
  private final UserService userService;
  private final ModelMapper modelMapper;

  @Autowired
  public PaymentServiceImpl(
      PaymentRepository paymentRepository,
      BankAccountRepository bankAccountRepository,
      UserService userService,
      ModelMapper modelMapper) {
    this.paymentRepository = paymentRepository;
    this.bankAccountRepository = bankAccountRepository;
    this.userService = userService;
    this.modelMapper = modelMapper;
  }

  @Override
  public Payment createSinglePayment(PaymentRequest paymentRequest) {

    if (isPaymentFromSameAccount(paymentRequest)) {
      throw new BusinessException(BusinessExceptionLabels.PAYMENT_INVALID_ACTION);
    }

    try {
      BankAccount giverBankAccount =
          bankAccountRepository
              .findByAccountNumber(paymentRequest.getGiverAccount())
              .orElseThrow(
                  () ->
                      new BusinessException(
                          BusinessExceptionLabels.BANK_ACCOUNT_NOT_FOUND_FOR_USER));

      validatePayment(paymentRequest, giverBankAccount);

      BigDecimal lastBalance =
          BigDecimal.valueOf(getAccountLastBalance(giverBankAccount).getAmount());
      BigDecimal requestedAmount = BigDecimal.valueOf(paymentRequest.getAmount());
      BigDecimal deductedAmount = lastBalance.subtract(requestedAmount);

      Balance balance =
          Balance.builder()
              .amount(deductedAmount.doubleValue())
              .currency(paymentRequest.getCurrency())
              .type(BalanceTypes.AVAILABLE)
              .build();

      Optional<BankAccount> beneficiaryAccount =
          bankAccountRepository.findByAccountNumber(paymentRequest.getBeneficiaryAccountNumber());

      if (beneficiaryAccount.isPresent()) {
        balance.setAmount(lastBalance.add(requestedAmount).doubleValue());
      }
      giverBankAccount.getBalances().add(balance);

      Payment payment =
          Payment.builder()
              .status(PaymentStatus.STATUS)
              .giverAccount(giverBankAccount)
              .communication(paymentRequest.getCommunication())
              .beneficiaryAccountNumber(paymentRequest.getBeneficiaryAccountNumber())
              .amount(paymentRequest.getAmount())
              .currency(paymentRequest.getCurrency())
              .build();

      return paymentRepository.save(payment);

    } catch (ApplicationException applicationException) {
      applicationException.printStackTrace();
      throw new BusinessException(BusinessExceptionLabels.APPLICATION_RELATED_ERROR);
    }
  }

  @Override
  public List<Payment> listPaymentsForUser(String username) {
    return paymentRepository.findAllByGiverAccount_Users_UsernameOrderByCreationDate(username);
  }

  @Override
  public List<Payment> listPaymentsForBeneficiary(String accountNumber) {
    return paymentRepository.findAllByBeneficiaryAccountNumberOrderByCreationDate(accountNumber);
  }

  @Override
  public void deletePayment(DeletePaymentRequest payload) {
    paymentRepository.delete(validatePayment(payload));
  }

  @Override
  public boolean isPaymentFromSameAccount(PaymentRequest paymentRequest) {
    return paymentRequest.getGiverAccount().equals(paymentRequest.getBeneficiaryAccountNumber());
  }

  @Override
  public boolean isPaymentExceedAvailableBalance(
      PaymentRequest paymentRequest, BankAccount giverBankAccount) throws ApplicationException {
    Balance balance =
        giverBankAccount.getBalances().stream()
            .skip(giverBankAccount.getBalances().size() - 1)
            .findFirst()
            .orElseThrow(
                () -> new ApplicationException(ApplicationExceptionLabels.INVALID_OPERATION));
    return balance.getAmount() < paymentRequest.getAmount();
  }

  @Override
  public boolean isAccountBlackListed(BankAccount bankAccount) {
    return bankAccount.getStatus().equals(BankAccountStatus.BLOCKED);
  }

  private void validatePayment(PaymentRequest paymentRequest, BankAccount giverBankAccount)
      throws ApplicationException {
    if (isAccountBlackListed(giverBankAccount)) {
      throw new BusinessException(BusinessExceptionLabels.PAYMENT_INVALID_ACTION);
    }

    if (isPaymentExceedAvailableBalance(paymentRequest, giverBankAccount)) {
      throw new BusinessException(BusinessExceptionLabels.PAYMENT_INVALID_ACTION);
    }

    if (!giverBankAccount.getUsers().contains(userService.getAuthenticatedUser())) {
      throw new BusinessException(BusinessExceptionLabels.GIVER_BANK_ACCOUNT_NOT_BELONGING_TO_USER);
    }
  }

  private Payment validatePayment(DeletePaymentRequest paymentPayload) {
    Payment payment =
        paymentRepository
            .findBy_id(paymentPayload.getPayment_Id())
            .orElseThrow(() -> new BusinessException(BusinessExceptionLabels.PAYMENT_NOT_FOUND));

    if (!payment.getGiverAccount().getUsers().contains(userService.getAuthenticatedUser())) {
      throw new BusinessException(BusinessExceptionLabels.GIVER_BANK_ACCOUNT_NOT_BELONGING_TO_USER);
    }
    return payment;
  }

  private Balance getAccountLastBalance(BankAccount account) throws ApplicationException {
    return account.getBalances().stream()
        .skip(account.getBalances().size() - 1)
        .findFirst()
        .orElseThrow(() -> new ApplicationException(ApplicationExceptionLabels.INVALID_OPERATION));
  }
}
