package com.web.banking.security.jwt.filter;

import com.web.banking.exception.BusinessException;
import com.web.banking.security.jwt.JwtService;
import com.web.banking.util.enums.BusinessExceptionLabels;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

  private final UserDetailsService userDetailsService;
  private final JwtService jwtService;

  @Autowired
  public JwtRequestFilter(UserDetailsService userDetailsService, JwtService jwtService) {
    this.userDetailsService = userDetailsService;
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String username = null;
    String token = null;
    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    SecurityContext securityContext = SecurityContextHolder.getContext();

    if (header != null && header.startsWith("Bearer ")) {
      token = header.substring(7);
      try {
        username = jwtService.getClaim(token, Claims::getSubject);
      } catch (IllegalArgumentException e) {
        log.error("Illegal argument :", e);
        throw new BusinessException(BusinessExceptionLabels.INVALID_TOKEN);
      } catch (ExpiredJwtException e) {
        log.error("ExpirationError :", e);
        throw new BusinessException(BusinessExceptionLabels.EXPIRED_TOKEN);
      }
    }

    if (username != null && securityContext.getAuthentication() == null) {
      UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
      if (jwtService.isValid(token, userDetails)) {
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(
                userDetails, token, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        securityContext.setAuthentication(authenticationToken);
      }
    }
    filterChain.doFilter(request, response);
  }
}
