package com.web.banking.rest.controller;

import com.web.banking.security.jwt.JwtRequest;
import com.web.banking.security.jwt.JwtResponse;
import com.web.banking.security.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final JwtService tokenService;
  private final UserDetailsService userDetailsService;

  @Autowired
  public AuthController(JwtService tokenService, UserDetailsService userDetailsService) {
    this.tokenService = tokenService;
    this.userDetailsService = userDetailsService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public JwtResponse createAuthenticationToken(@RequestBody @Valid JwtRequest jwtRequest) {
    tokenService.authenticate(jwtRequest.getUsername(), jwtRequest.getPassword());
    UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.getUsername());
    return JwtResponse.builder().token(tokenService.generate(userDetails)).build();
  }

  @PostMapping("/revoke")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public boolean revokeToken(Authentication authentication) {
    // Our token as credentials
    tokenService.revoke(authentication.getCredentials().toString());
    return true;
  }
}
