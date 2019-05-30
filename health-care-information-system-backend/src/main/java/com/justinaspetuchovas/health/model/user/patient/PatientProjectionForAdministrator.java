package com.justinaspetuchovas.health.model.user.patient;

import java.util.UUID;

/**
 * Partial view of a patient used to return information about the patient when a user is logged in
 * as an administrator.
 */
public interface PatientProjectionForAdministrator {
  UUID getId();

  String getFirstName();

  String getLastName();

  String getUsername();

  String getPersonalIdentificationNumber();
}
