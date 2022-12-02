package com.web.banking.rest.controller;

import com.web.banking.entity.Payment;
import com.web.banking.rest.payload.DeletePaymentRequest;
import com.web.banking.rest.payload.PaymentRequest;
import com.web.banking.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

  private final PaymentService paymentService;

  @Autowired
  public PaymentController(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<Payment> listPaymentsForUser() {
    return this.paymentService.listPaymentsForUser("john.doe");
  }

  @GetMapping("/beneficiary")
  @ResponseStatus(HttpStatus.OK)
  public List<Payment> listPaymentsForBeneficiary(@RequestParam @NotNull String beneficiary) {
    return this.paymentService.listPaymentsForBeneficiary(beneficiary);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Payment create(@RequestBody @Valid PaymentRequest payload) {
    return this.paymentService.createSinglePayment(payload);
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void delete(@RequestBody @Valid DeletePaymentRequest payload) {
    this.paymentService.deletePayment(payload);
  }
}
