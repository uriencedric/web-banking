package com.web.banking.service.impl;

import com.web.banking.entity.Balance;
import com.web.banking.entity.BankAccount;
import com.web.banking.entity.Payment;
import com.web.banking.entity.User;
import com.web.banking.exception.BusinessException;
import com.web.banking.repository.BankAccountRepository;
import com.web.banking.repository.PaymentRepository;
import com.web.banking.rest.payload.DeletePaymentRequest;
import com.web.banking.rest.payload.PaymentRequest;
import com.web.banking.util.enums.BankAccountStatus;
import com.web.banking.util.enums.Currencies;
import com.web.banking.util.enums.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

  @Mock PaymentRepository paymentRepository;
  @Mock BankAccountRepository bankAccountRepository;
  @Mock UserServiceImpl userService;
  @Mock ModelMapper modelMapper;
  @InjectMocks PaymentServiceImpl paymentService;

  private PaymentRequest paymentRequest;
  private BankAccount giverBankAccount;
  private BankAccount beneficiaryBankAccount;
  private Payment payment;
  private User giver;
  private Balance giverBalance;

  @BeforeEach
  void setUp() {
    giver = User.builder().address("address").username("giver").build();
    User beneficiary = User.builder().address("address").username("beneficiary").build();

    String beneficiaryAcct = "00000";
    String giverAcct = "11111";

    paymentRequest =
        PaymentRequest.builder()
            .amount(100)
            .beneficiaryAccountNumber(beneficiaryAcct)
            .communication("Hello")
            .currency(Currencies.EUR)
            .giverAccount(giverAcct)
            .status(PaymentStatus.STATUS)
            .build();

    beneficiaryBankAccount =
        BankAccount.builder()
            .id(1L)
            .status(BankAccountStatus.ENABLED)
            .balances(new ArrayList<>())
            .users(Collections.singletonList(beneficiary))
            .accountNumber(beneficiaryAcct)
            .build();

    giverBankAccount =
        BankAccount.builder()
            .id(1L)
            .status(BankAccountStatus.ENABLED)
            .balances(new ArrayList<>())
            .users(Collections.singletonList(giver))
            .accountNumber(giverAcct)
            .build();

    payment =
        Payment.builder()
            .amount(paymentRequest.getAmount())
            .beneficiaryAccountNumber(paymentRequest.getBeneficiaryAccountNumber())
            .communication(paymentRequest.getCommunication())
            .currency(paymentRequest.getCurrency())
            .giverAccount(giverBankAccount)
            .status(paymentRequest.getStatus())
            .build();

    giverBalance = Balance.builder().id(1L).amount(1000).currency(Currencies.EUR).build();
  }

  @Test
  void when_providing_wrong_account_user_should_throw_ex() {
    paymentRequest.setGiverAccount("22");
    when(bankAccountRepository.findByAccountNumber(paymentRequest.getGiverAccount()))
        .thenReturn(Optional.empty());
    assertThrows(BusinessException.class, () -> paymentService.createSinglePayment(paymentRequest));
  }

  @Test
  void when_providing_wrong_auth_user_should_throw_ex() {
    giverBalance.setAmount(1000);
    giverBankAccount.setBalances(Collections.singletonList(giverBalance));
    when(userService.getAuthenticatedUser()).thenReturn(new User());
    when(bankAccountRepository.findByAccountNumber(paymentRequest.getGiverAccount()))
        .thenReturn(Optional.ofNullable(giverBankAccount));
    assertThrows(BusinessException.class, () -> paymentService.createSinglePayment(paymentRequest));
  }

  @Test
  void when_providing_correct_auth_user_should_return_payment() {
    giverBalance.setAmount(1000);
    giverBankAccount.setBalances(Collections.singletonList(giverBalance));
    when(userService.getAuthenticatedUser()).thenReturn(giver);
    when(bankAccountRepository.findByAccountNumber(paymentRequest.getGiverAccount()))
        .thenReturn(Optional.ofNullable(giverBankAccount));
    when(bankAccountRepository.findByAccountNumber(paymentRequest.getBeneficiaryAccountNumber()))
        .thenReturn(Optional.ofNullable(beneficiaryBankAccount));
    when(paymentRepository.save(payment)).thenReturn(payment);
    assertNotNull(paymentService.createSinglePayment(paymentRequest));
  }

  @Test
  void when_providing_correct_auth_user_should_increase_balance_count() {
    giverBankAccount.getBalances().add(giverBalance);
    when(userService.getAuthenticatedUser()).thenReturn(giver);
    when(bankAccountRepository.findByAccountNumber(paymentRequest.getGiverAccount()))
        .thenReturn(Optional.ofNullable(giverBankAccount));
    when(bankAccountRepository.findByAccountNumber(paymentRequest.getBeneficiaryAccountNumber()))
        .thenReturn(Optional.ofNullable(beneficiaryBankAccount));
    when(paymentRepository.save(payment)).thenReturn(payment);
    assertEquals(
        2,
        paymentService.createSinglePayment(paymentRequest).getGiverAccount().getBalances().size());
  }

  @Test
  void when_executing_valid_payment_and_beneficiary_is_in_bank_should_increase_balance() {
    giverBalance.setAmount(1000);
    giverBankAccount.setBalances(Collections.singletonList(giverBalance));
    when(userService.getAuthenticatedUser()).thenReturn(giver);
    when(bankAccountRepository.findByAccountNumber(paymentRequest.getGiverAccount()))
        .thenReturn(Optional.ofNullable(giverBankAccount));
    when(bankAccountRepository.findByAccountNumber(paymentRequest.getBeneficiaryAccountNumber()))
        .thenReturn(Optional.ofNullable(beneficiaryBankAccount));
    when(paymentRepository.save(payment)).thenReturn(payment);
    Payment singlePayment = paymentService.createSinglePayment(paymentRequest);
    List<Balance> balances = singlePayment.getGiverAccount().getBalances();
    assertEquals(1100, balances.get(balances.size() - 1).getAmount());
  }

  @Test
  void when_executing_valid_payment_and_beneficiary_is_not_in_bank_should_decrease_balance() {
    giverBalance.setAmount(1000);
    beneficiaryBankAccount.setAccountNumber("33333");
    giverBankAccount.setBalances(Collections.singletonList(giverBalance));
    when(userService.getAuthenticatedUser()).thenReturn(giver);
    when(bankAccountRepository.findByAccountNumber(paymentRequest.getGiverAccount()))
        .thenReturn(Optional.ofNullable(giverBankAccount));
    when(bankAccountRepository.findByAccountNumber(paymentRequest.getBeneficiaryAccountNumber()))
        .thenReturn(Optional.empty());
    when(paymentRepository.save(payment)).thenReturn(payment);
    Payment singlePayment = paymentService.createSinglePayment(paymentRequest);
    List<Balance> balances = singlePayment.getGiverAccount().getBalances();
    assertEquals(900, balances.get(balances.size() - 1).getAmount());
  }

  @Test
  void when_list_payment_for_current_user_should_return_list() {
    when(paymentRepository.findAllByGiverAccount_Users_UsernameOrderByCreationDate(
            giver.getUsername()))
        .thenReturn(Collections.singletonList(payment));
    assertEquals(1, paymentService.listPaymentsForUser(giver.getUsername()).size());
  }

  @Test
  void when_delete_existing_payment_should_not_throw_ex() {
    DeletePaymentRequest paymentPayload =
        DeletePaymentRequest.builder().payment_Id("ER332049IOLP").build();
    when(paymentRepository.findBy_id(paymentPayload.getPayment_Id()))
        .thenReturn(Optional.ofNullable(payment));
    assertDoesNotThrow(() -> paymentService.deletePayment(paymentPayload));
  }

  @Test
  void when_delete_non_existing_payment_should_throw_ex() {
    DeletePaymentRequest paymentPayload = DeletePaymentRequest.builder().payment_Id("0").build();
    when(paymentRepository.findBy_id(paymentPayload.getPayment_Id())).thenReturn(Optional.empty());
    assertThrows(BusinessException.class, () -> paymentService.deletePayment(paymentPayload));
  }

  @Test
  void when_payment_from_the_same_acct_should_throw_ex() {
    paymentRequest.setGiverAccount("11");
    paymentRequest.setBeneficiaryAccountNumber("11");
    assertThrows(BusinessException.class, () -> paymentService.createSinglePayment(paymentRequest));
  }

  @Test
  void when_payment_amount_exceed_account_amount_should_throw_ex() {
    paymentRequest.setAmount(10000);
    giverBankAccount.setBalances(Collections.singletonList(giverBalance));
    when(bankAccountRepository.findByAccountNumber(paymentRequest.getGiverAccount()))
        .thenReturn(Optional.ofNullable(giverBankAccount));
    assertThrows(BusinessException.class, () -> paymentService.createSinglePayment(paymentRequest));
  }

  @Test
  void when_payment_to_forbidden_account_should_throw_ex() {
    giverBankAccount.setStatus(BankAccountStatus.BLOCKED);
    when(bankAccountRepository.findByAccountNumber(paymentRequest.getGiverAccount()))
        .thenReturn(Optional.ofNullable(giverBankAccount));
    assertThrows(BusinessException.class, () -> paymentService.createSinglePayment(paymentRequest));
  }
}
