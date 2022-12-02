package com.web.banking.service;

import com.web.banking.entity.User;
import org.springframework.security.core.Authentication;

public interface UserService {
  User findByUsername(String username);

  User getAuthenticatedUser();

  void setAuthentication(Authentication authentication);

  boolean isConnected();

  User updateUser();
}
