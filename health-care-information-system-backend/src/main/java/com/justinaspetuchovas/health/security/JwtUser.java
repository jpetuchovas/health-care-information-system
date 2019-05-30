package com.justinaspetuchovas.health.security;

import com.justinaspetuchovas.health.model.user.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

/**
 * Authenticated user with additional details such as his ID, full name, role and
 * last password reset date.
 */
public class JwtUser implements UserDetails {
  private UUID id;
  private String username;
  private String password;
  private Collection<? extends GrantedAuthority> authorities;
  private Date lastPasswordResetDate;
  private Role role;
  private String name;

  public JwtUser(
      UUID id,
      String username,
      String password,
      Collection<? extends GrantedAuthority> authorities,
      Date lastPasswordResetDate,
      Role role,
      String name
  ) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.authorities = authorities;
    this.lastPasswordResetDate = lastPasswordResetDate;
    this.role = role;
    this.name = name;
  }

  public UUID getId() {
    return id;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public Date getLastPasswordResetDate() {
    return lastPasswordResetDate;
  }

  public Role getRole() {
    return role;
  }

  public String getName() {
    return name;
  }
}
