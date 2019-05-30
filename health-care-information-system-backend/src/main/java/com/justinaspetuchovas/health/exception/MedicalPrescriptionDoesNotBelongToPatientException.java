package com.justinaspetuchovas.health.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when trying to mark a prescription that does not belong to the patient as used.
 *
 * <p>Returns an HTTP 400 Bad Request status.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MedicalPrescriptionDoesNotBelongToPatientException extends RuntimeException {
  public MedicalPrescriptionDoesNotBelongToPatientException() {
    super("Medical prescription does not belong to the specified patient.");
  }
}
