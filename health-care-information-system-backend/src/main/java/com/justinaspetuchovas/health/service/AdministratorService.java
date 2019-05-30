package com.justinaspetuchovas.health.service;

import com.justinaspetuchovas.health.common.TimeProvider;
import com.justinaspetuchovas.health.exception.UsernameConflictException;
import com.justinaspetuchovas.health.model.user.administrator.Administrator;
import com.justinaspetuchovas.health.model.user.administrator.NewAdministratorCommand;
import com.justinaspetuchovas.health.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service used to perform operations with administrators.
 */
@Service
public class AdministratorService {
  private static final Logger logger = LogManager.getLogger(AdministratorService.class);
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final TimeProvider timeProvider;

  @Autowired
  public AdministratorService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      TimeProvider timeProvider
  ) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.timeProvider = timeProvider;
  }

  @Transactional
  public void createAdministrator(NewAdministratorCommand newAdministratorCommand) {
    String username = newAdministratorCommand.getUsername();

    if (userRepository.existsByUsername(username)) {
      logger.info(
          "Could not create an administrator with the username \"{}\" because a user with "
              + "such a username already exists.",
          username
      );
      throw new UsernameConflictException();
    } else {
      userRepository.save(
          new Administrator(
              username,
              passwordEncoder.encode(newAdministratorCommand.getPassword()),
              newAdministratorCommand.getFirstName(),
              newAdministratorCommand.getLastName(),
              timeProvider.now()
          )
      );

      logger.info("Created an administrator with the username \"{}\".", username);
    }
  }
}
