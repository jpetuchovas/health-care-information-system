package com.justinaspetuchovas.health.model.record;

import java.util.Date;
import java.util.UUID;

/**
 * Partial view of a medical record.
 */
public interface MedicalRecordProjection {
  UUID getId();

  String getDescription();

  short getVisitDurationInMinutes();

  String getDiseaseCode();

  boolean getIsVisitCompensated();

  boolean getIsVisitRepeated();

  Date getDate();

  String getDoctorFullName();
}
