package com.justinaspetuchovas.health.filter;

import com.justinaspetuchovas.health.pagination.PageCommand;

/**
 * Filter used to search for users.
 */
public abstract class UserFilter extends PageCommand {
  private String firstName;
  private String lastName;

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName.trim();
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName.trim();
  }
}
