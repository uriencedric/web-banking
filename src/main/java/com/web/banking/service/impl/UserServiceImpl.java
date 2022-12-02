package com.web.banking.service.impl;

import com.web.banking.entity.User;
import com.web.banking.exception.BusinessException;
import com.web.banking.repository.UserRepository;
import com.web.banking.service.UserService;
import com.web.banking.util.enums.BusinessExceptionLabels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  final UserRepository userRepository;

  @Autowired
  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public User findByUsername(String username) {
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new BusinessException(BusinessExceptionLabels.USER_NOT_FOUND));
  }

  public User getAuthenticatedUser() {
    // todo = for testing purposes before auth
    return userRepository.findByUsername("john.doe").get();
    // return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

  @Override
  public void setAuthentication(Authentication authentication) {
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  @Override
  public boolean isConnected() {
    return SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
  }

  @Override
  public User updateUser() {
    return null;
  }
}
