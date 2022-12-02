package com.web.banking.security;

import com.web.banking.entity.User;
import com.web.banking.service.UserService;
import com.web.banking.util.constants.ApplicationRoles;
import com.web.banking.util.enums.BusinessExceptionLabels;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.springframework.security.core.userdetails.User.withUsername;

public class ApplicationUserDetailsService implements UserDetailsService {

  private final UserService userService;

  public ApplicationUserDetailsService(UserService userService) {
    this.userService = userService;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userService.findByUsername(username);

    if (user == null) {
      throw new UsernameNotFoundException(BusinessExceptionLabels.USER_NOT_FOUND.getValue());
    }
    UserBuilder builder = withUsername(user.getUsername());
    builder.password(new BCryptPasswordEncoder().encode(user.getPassword()));
    builder.roles(ApplicationRoles.USER_ROLE_ARRAY);
    return builder.build();
  }
}
