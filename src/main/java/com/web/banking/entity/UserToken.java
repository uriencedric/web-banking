package com.web.banking.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UserToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String token;

  @ManyToOne(
      targetEntity = User.class,
      cascade = {CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE})
  private User user;

  private boolean isRevoked;
}
