package com.justinaspetuchovas.health.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an authenticated user tries to retrieve information he has no access to.
 *
 * <p>Returns an HTTP 403 Forbidden status.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {
  public ForbiddenException() {
    super("Access is denied.");
  }
}
