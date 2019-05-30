package com.justinaspetuchovas.health.filter;

/**
 * Filter used to search for patients if a user is logged in as a doctor.
 */
public class PatientFilterForDoctor extends UserFilter {
  private String personalIdentificationNumber;
  private String diseaseCode;

  public String getPersonalIdentificationNumber() {
    return personalIdentificationNumber;
  }

  public void setPersonalIdentificationNumber(String personalIdentificationNumber) {
    this.personalIdentificationNumber = personalIdentificationNumber.trim();
  }

  public String getDiseaseCode() {
    return diseaseCode;
  }

  public void setDiseaseCode(String diseaseCode) {
    this.diseaseCode = diseaseCode.trim();
  }
}
