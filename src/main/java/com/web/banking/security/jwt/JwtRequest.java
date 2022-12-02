package com.web.banking.security.jwt;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class JwtRequest {
  @NotNull private String username;
  @NotNull private String password;
}
