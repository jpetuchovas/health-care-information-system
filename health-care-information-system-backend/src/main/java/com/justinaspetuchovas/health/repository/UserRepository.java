package com.justinaspetuchovas.health.repository;

import com.justinaspetuchovas.health.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

/**
 * Repository for {@link User} entities.
 */
public interface UserRepository extends JpaRepository<User, UUID> {
  User findByUsername(String username);

  @Query(
      "SELECT CASE WHEN COUNT(user.id) > 0 THEN TRUE ELSE FALSE END "
          + "FROM User AS user "
          + "WHERE user.username = :username"
  )
  boolean existsByUsername(@Param("username") String username);
}
