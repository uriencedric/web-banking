package com.web.banking.repository;

import com.web.banking.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
  Optional<List<BankAccount>> findAllByUsers_Username(String username);

  Optional<BankAccount> findByAccountNumber(String accountNumber);
}
