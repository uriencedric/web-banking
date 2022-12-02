package com.web.banking.security.jwt;

import com.web.banking.entity.User;
import com.web.banking.entity.UserToken;
import com.web.banking.exception.BusinessException;
import com.web.banking.properties.JwtProperties;
import com.web.banking.repository.UserRepository;
import com.web.banking.repository.UserTokenRepository;
import com.web.banking.util.enums.BusinessExceptionLabels;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Component
public class JwtService {
  public static final long VALIDITY = 300000;
  private final JwtProperties properties;
  private final AuthenticationManager manager;
  private final UserTokenRepository userTokenRepository;
  private final UserRepository userRepository;

  @Autowired
  public JwtService(
      JwtProperties properties,
      AuthenticationManager manager,
      UserTokenRepository userTokenRepository,
      UserRepository userRepository) {
    this.properties = properties;
    this.manager = manager;
    this.userTokenRepository = userTokenRepository;
    this.userRepository = userRepository;
  }

  public <T> T getClaim(String token, Function<Claims, T> lambda) {
    Claims claims =
        Jwts.parser().setSigningKey(properties.getSecret()).parseClaimsJws(token).getBody();
    return lambda.apply(claims);
  }

  private boolean isExpired(String token) {
    Date expiration = getClaim(token, Claims::getExpiration);
    return expiration.before(new Date());
  }

  public String generate(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    long currentTimeMillis = System.currentTimeMillis();

    String token =
        Jwts.builder()
            .setClaims(claims)
            .setIssuer(properties.getIssuer())
            .setSubject(userDetails.getUsername())
            .signWith(SignatureAlgorithm.HS512, properties.getSecret())
            .setIssuedAt(new Date(currentTimeMillis))
            .setExpiration(new Date(currentTimeMillis + VALIDITY))
            .compact();

    userRepository
        .findByUsername(userDetails.getUsername())
        .ifPresent(user -> saveToken(token, user));

    return token;
  }

  public boolean isValid(String token, UserDetails userDetails) {
    return (getClaim(token, Claims::getSubject).equals(userDetails.getUsername())
        && !isExpired(token)
        && !isRevoked(token));
  }

  private boolean isRevoked(String token) {
    Optional<UserToken> userToken = userTokenRepository.findByToken(token);
    return userToken.map(UserToken::isRevoked).orElse(false);
  }

  public void authenticate(String username, String password) {
    try {
      manager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    } catch (DisabledException e) {
      log.error("Auth error", e);
      throw new BusinessException(BusinessExceptionLabels.USER_NOT_FOUND);
    } catch (BadCredentialsException e) {
      log.error("Auth error", e);
      throw new BusinessException(BusinessExceptionLabels.BAD_CREDENTIALS);
    }
  }

  public void revoke(String token) {
    Optional<UserToken> tokenOptional = userTokenRepository.findByToken(token);
    tokenOptional.ifPresent(
        userToken -> {
          userToken.setRevoked(true);
          userTokenRepository.save(userToken);
        });
  }

  public void saveToken(String token, User user) {
    UserToken userToken = UserToken.builder().token(token).user(user).build();

    userTokenRepository.save(userToken);
  }
}
