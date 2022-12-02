package com.web.banking.repository;

import com.web.banking.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
  Optional<UserToken> findByToken(String token);
}
