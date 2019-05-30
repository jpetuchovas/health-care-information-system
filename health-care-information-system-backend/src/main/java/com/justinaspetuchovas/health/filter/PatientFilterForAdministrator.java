package com.justinaspetuchovas.health.filter;

/**
 * Filter used to search for patients if a user is logged in as an administrator.
 */
public class PatientFilterForAdministrator extends UserFilter {
  private String username;
  private String personalIdentificationNumber;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username.trim();
  }

  public String getPersonalIdentificationNumber() {
    return personalIdentificationNumber;
  }

  public void setPersonalIdentificationNumber(String personalIdentificationNumber) {
    this.personalIdentificationNumber = personalIdentificationNumber.trim();
  }
}
