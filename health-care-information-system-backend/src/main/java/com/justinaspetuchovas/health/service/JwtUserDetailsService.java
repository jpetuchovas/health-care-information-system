package com.justinaspetuchovas.health.service;

import com.justinaspetuchovas.health.security.SecurityConstants;
import com.justinaspetuchovas.health.model.user.User;
import com.justinaspetuchovas.health.repository.UserRepository;
import com.justinaspetuchovas.health.security.JwtUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Collections;

/**
 * Service used to load users.
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {
  private static final Logger logger = LogManager.getLogger(JwtUserDetailsService.class);
  private final UserRepository userRepository;

  @Autowired
  public JwtUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username);

    if (user == null) {
      logger.warn(
          "Could not return a user with the username \"{}\" because "
              + "such a username does not exist.",
          username
      );
      throw new UsernameNotFoundException(
          MessageFormat.format("No user found with the username {0}.", username)
      );
    } else {
      logger.debug("Returned the user with the username \"{}\".", username);

      return new JwtUser(
          user.getId(),
          user.getUsername(),
          user.getPassword(),
          Collections.singletonList(
              new SimpleGrantedAuthority(SecurityConstants.ROLE_PREFIX + user.getRole().name())
          ),
          user.getLastPasswordResetDate(),
          user.getRole(),
          MessageFormat.format("{0} {1}", user.getFirstName(), user.getLastName())
      );
    }
  }
}
