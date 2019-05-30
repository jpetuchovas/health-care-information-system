package com.justinaspetuchovas.health.model.user.patient;

import java.util.Date;
import java.util.UUID;

/**
 * Partial view of a patient used to return information about the patient when a user is logged in
 * as a doctor or pharmacist.
 */
public interface PatientProjectionForDoctorOrPharmacist {
  UUID getId();

  String getFirstName();

  String getLastName();

  String getPersonalIdentificationNumber();

  Date getBirthDate();
}
