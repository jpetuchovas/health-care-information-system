package com.justinaspetuchovas.health.service;

import com.justinaspetuchovas.health.common.TimeProvider;
import com.justinaspetuchovas.health.model.user.User;
import com.justinaspetuchovas.health.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service used for user's password changing.
 */
@Service
public class PasswordService {
  private static final Logger logger = LogManager.getLogger(PasswordService.class);
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final TimeProvider timeProvider;

  @Autowired
  public PasswordService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      TimeProvider timeProvider
  ) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.timeProvider = timeProvider;
  }

  @Transactional
  public void changePassword(String username, String newPassword) {
    User user = userRepository.findByUsername(username);
    user.setPassword(passwordEncoder.encode(newPassword));
    user.setLastPasswordResetDate(timeProvider.now());
    logger.info("Changed the user's password.");
  }
}
