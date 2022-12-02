package com.web.banking.repository;

import com.web.banking.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
  Optional<Payment> findBy_id(String _id);

  List<Payment> findAllByGiverAccount_Users_UsernameOrderByCreationDate(String username);

  List<Payment> findAllByBeneficiaryAccountNumberOrderByCreationDate(String accountNumber);
}
