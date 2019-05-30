package com.justinaspetuchovas.health.model.user.doctor;

import java.util.UUID;

/**
 * Partial view of a doctor.
 */
public interface DoctorProjection {
  UUID getId();

  String getFirstName();

  String getLastName();

  String getUsername();

  String getSpecialization();
}
