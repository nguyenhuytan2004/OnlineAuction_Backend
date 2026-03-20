package com.example.backend.security;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.backend.entity.User;

public class CustomUserDetails implements UserDetails {

  private final User user;

  public CustomUserDetails(User user) {
    this.user = user;
  }

  @Override
  public List<GrantedAuthority> getAuthorities() {
    return List
        .of(new SimpleGrantedAuthority("ROLE_" + (user.getRole() != null ? user.getRole().name() : "BIDDER")));
  }

  @Override
  public String getPassword() {
    return user.getEncryptedPassword();
  }

  @Override
  public String getUsername() {
    return String.valueOf(user.getUserId());
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

  public User getUser() {
    return user;
  }

}
