package com.justinaspetuchovas.health.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when trying to mark a no longer valid medical prescription as used.
 *
 * <p>Returns an HTTP 400 Bad Request status.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MedicalPrescriptionValidityEndedException extends RuntimeException {
  public MedicalPrescriptionValidityEndedException() {
    super("Medical prescription validity ended.");
  }
}
