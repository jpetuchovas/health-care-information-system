package com.justinaspetuchovas.health.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when trying to register a user with a future birth date.
 *
 * <p>Returns an HTTP 400 Bad Request status.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FutureDateException extends RuntimeException {
  public FutureDateException() {
    super("Cannot be a future date.");
  }
}
