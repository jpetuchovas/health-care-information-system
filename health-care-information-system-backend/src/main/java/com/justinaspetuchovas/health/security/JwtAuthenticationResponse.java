package com.justinaspetuchovas.health.security;

/**
 * Response used to return a JSON web token.
 */
public class JwtAuthenticationResponse {
  private String token;

  public JwtAuthenticationResponse(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
