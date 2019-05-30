package com.justinaspetuchovas.health.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when trying to mark a medical prescription that does not exist as used.
 *
 * <p>Returns an HTTP 400 Bad Request status.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MedicalPrescriptionNotFoundException extends RuntimeException {
  public MedicalPrescriptionNotFoundException() {
    super("Medical prescription does not exist.");
  }
}
