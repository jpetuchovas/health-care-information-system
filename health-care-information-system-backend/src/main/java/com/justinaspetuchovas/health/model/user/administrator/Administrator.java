package com.justinaspetuchovas.health.model.user.administrator;

import com.justinaspetuchovas.health.model.user.Role;
import com.justinaspetuchovas.health.model.user.User;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Entity that represents an administrator.
 */
@Entity
@Table(name = "administrators")
public class Administrator extends User {
  public Administrator() {
  }

  public Administrator(
      String username,
      String password,
      String firstName,
      String lastName,
      Date lastPasswordResetDate
  ) {
    super(username, password, firstName, lastName, lastPasswordResetDate, Role.ADMIN);
  }
}
