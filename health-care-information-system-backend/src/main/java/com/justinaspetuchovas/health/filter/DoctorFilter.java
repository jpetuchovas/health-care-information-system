package com.justinaspetuchovas.health.filter;

/**
 * Filter used to search for doctors.
 */
public class DoctorFilter extends UserFilter {
  private String username;
  private String specialization;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username.trim();
  }

  public String getSpecialization() {
    return specialization;
  }

  public void setSpecialization(String specialization) {
    this.specialization = specialization.trim();
  }
}
