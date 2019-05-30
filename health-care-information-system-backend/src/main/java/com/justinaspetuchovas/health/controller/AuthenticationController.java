package com.justinaspetuchovas.health.controller;

import com.justinaspetuchovas.health.model.user.PasswordChanger;
import com.justinaspetuchovas.health.security.JwtAuthenticationRequest;
import com.justinaspetuchovas.health.security.JwtAuthenticationResponse;
import com.justinaspetuchovas.health.security.JwtUser;
import com.justinaspetuchovas.health.security.JwtUtil;
import com.justinaspetuchovas.health.service.PasswordService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that handles requests to change a user's password and authenticate.
 */
@RestController
@RequestMapping("/api")
public class AuthenticationController {
  private static final Logger logger = LogManager.getLogger(AuthenticationController.class);
  private final AuthenticationManager authenticationManager;
  private final PasswordService passwordService;
  private final JwtUtil jwtUtil;

  @Autowired
  public AuthenticationController(
      AuthenticationManager authenticationManager,
      PasswordService passwordService,
      JwtUtil jwtUtil
  ) {
    this.authenticationManager = authenticationManager;
    this.passwordService = passwordService;
    this.jwtUtil = jwtUtil;
  }

  @PostMapping("/login")
  public ResponseEntity<JwtAuthenticationResponse> createAuthenticationToken(
      @RequestBody JwtAuthenticationRequest jwtAuthenticationRequest
  ) {
    Authentication authentication;
    String username = jwtAuthenticationRequest.getUsername();

    try {
      authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              username,
              jwtAuthenticationRequest.getPassword()
          )
      );
    } catch (AuthenticationException exception) {
      logger.info(
          "Login was unsuccessful because either a user with the username \"{}\" does not exist or "
              + "the password was incorrect.",
          username
      );
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    SecurityContextHolder.getContext().setAuthentication(authentication);

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    logger.info("The user with the username \"{}\" successfully logged in.", username);
    return ResponseEntity.ok(
        new JwtAuthenticationResponse(jwtUtil.generateToken((JwtUser) userDetails))
    );
  }

  @GetMapping("/refresh")
  public ResponseEntity<JwtAuthenticationResponse> refreshAuthenticationToken(
      @RequestHeader("${jwt.header}") String jwtHeader,
      Authentication authentication
  ) {
    String token = jwtUtil.getTokenFromRequestHeader(jwtHeader);
    JwtUser user = (JwtUser) authentication.getPrincipal();

    if (jwtUtil.canTokenBeRefreshed(token, user.getLastPasswordResetDate())) {
      String refreshedToken = jwtUtil.refreshToken(token);
      logger.info("Successfully refreshed the JWT.");
      return ResponseEntity.ok(new JwtAuthenticationResponse(refreshedToken));
    } else {
      logger.warn("The JWT \"{}\" refresh was unsuccessful.", token);
      return ResponseEntity.badRequest().body(null);
    }
  }

  @PostMapping("/password-change")
  public JwtAuthenticationResponse changePassword(
      @RequestBody PasswordChanger passwordChanger,
      Authentication authentication
  ) {
    JwtUser user = (JwtUser) authentication.getPrincipal();
    String username = user.getUsername();

    logger.debug("Re-authenticating the user for the password change request.");
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(username, passwordChanger.getOldPassword())
    );

    passwordService.changePassword(username, passwordChanger.getNewPassword());

    return new JwtAuthenticationResponse(jwtUtil.generateToken(user));
  }
}
