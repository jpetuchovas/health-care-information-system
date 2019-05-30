package com.justinaspetuchovas.health.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown to indicate the request could not be completed because conflict with some rule.
 *
 * <p>Returns an HTTP 409 Conflict status.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceConflictException extends RuntimeException {
  public ResourceConflictException(String message) {
    super(message);
  }
}
