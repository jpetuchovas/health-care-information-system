package com.justinaspetuchovas.health;

import com.justinaspetuchovas.health.common.TimeProvider;
import com.justinaspetuchovas.health.model.user.administrator.Administrator;
import com.justinaspetuchovas.health.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Loader of the first administrator.
 *
 * <p>Creates the first administrator with username "admin" if doesn't exist,
 * does nothing otherwise.
 */
@Component
public class FirstAdministratorLoader implements CommandLineRunner {
  private final UserRepository userRepository;
  private final TimeProvider timeProvider;

  @Autowired
  public FirstAdministratorLoader(UserRepository userRepository, TimeProvider timeProvider) {
    this.userRepository = userRepository;
    this.timeProvider = timeProvider;
  }

  @Override
  public void run(String... strings) throws Exception {
    String username = "admin";

    if (!userRepository.existsByUsername(username)) {
      userRepository.save(
          new Administrator(
              username,
              "$2a$10$yZ0T9uGpA8o2vb7Q156wP.tYs.vbo0zLuc1KAHNIGVmB.iMeNUWTK",
              "Vardenis",
              "Pavardenis",
              timeProvider.now()
          )
      );
    }
  }
}
