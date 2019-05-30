package com.justinaspetuchovas.health.filter;

/**
 * Filter used to search for users with the specified personal identification number.
 */
public class PersonalIdentificationNumberFilter {
  private String personalIdentificationNumber;

  public String getPersonalIdentificationNumber() {
    return personalIdentificationNumber;
  }

  public void setPersonalIdentificationNumber(String personalIdentificationNumber) {
    this.personalIdentificationNumber = personalIdentificationNumber.trim();
  }
}
