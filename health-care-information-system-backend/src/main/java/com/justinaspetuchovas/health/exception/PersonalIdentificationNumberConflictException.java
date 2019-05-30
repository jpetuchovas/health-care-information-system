package com.justinaspetuchovas.health.exception;

/**
 * Exception thrown when trying to register a user with a personal identification number that
 * already exists.
 */
public class PersonalIdentificationNumberConflictException extends ResourceConflictException {
  public PersonalIdentificationNumberConflictException() {
    super("Personal identification code already exists.");
  }
}
