package com.justinaspetuchovas.health.common;

import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Current time provider.
 */
@Component
public class TimeProvider {
  /**
   * Provides the current time.
   *
   * @return an initialized <code>Date</code> object so that it represents the time at which
   *         it was initialized
   */
  public Date now() {
    return new Date();
  }
}
