package com.justinaspetuchovas.health.exception;

/**
 * Exception thrown when trying to register a user with a username that already exists.
 */
public class UsernameConflictException extends ResourceConflictException {
  public UsernameConflictException() {
    super("Username already exists.");
  }
}
