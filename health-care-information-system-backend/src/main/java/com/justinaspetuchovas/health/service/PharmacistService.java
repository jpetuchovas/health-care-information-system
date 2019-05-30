package com.justinaspetuchovas.health.service;

import com.justinaspetuchovas.health.common.TimeProvider;
import com.justinaspetuchovas.health.exception.UsernameConflictException;
import com.justinaspetuchovas.health.model.user.pharmacist.NewPharmacistCommand;
import com.justinaspetuchovas.health.model.user.pharmacist.Pharmacist;
import com.justinaspetuchovas.health.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service used to perform operations with pharmacists.
 */
@Service
public class PharmacistService {
  private static final Logger logger = LogManager.getLogger(PharmacistService.class);
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final TimeProvider timeProvider;

  @Autowired
  public PharmacistService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      TimeProvider timeProvider
  ) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.timeProvider = timeProvider;
  }

  @Transactional
  public void createPharmacist(NewPharmacistCommand newPharmacistCommand) {
    String username = newPharmacistCommand.getUsername();
    if (userRepository.existsByUsername(username)) {
      logger.info(
          "Could not create a pharmacist with the username \"{}\" because a user with "
              + "such a username already exists.",
          username
      );
      throw new UsernameConflictException();
    } else {
      userRepository.save(
          new Pharmacist(
              username,
              passwordEncoder.encode(newPharmacistCommand.getPassword()),
              newPharmacistCommand.getFirstName(),
              newPharmacistCommand.getLastName(),
              timeProvider.now(),
              newPharmacistCommand.getWorkplace()
          )
      );

      logger.info("Created a pharmacist with the username \"{}\".", username);
    }
  }
}
