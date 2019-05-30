package com.justinaspetuchovas.health.model.user.patient;

import java.util.Date;

/**
 * Partial view of a patient used to return information about the patient for the CSV export with
 * a doctor's patient list.
 */
public interface PatientProjectionForCsvDownload {
  String getFirstName();

  String getLastName();

  String getPersonalIdentificationNumber();

  Date getBirthDate();
}
