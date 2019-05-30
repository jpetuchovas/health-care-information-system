package com.justinaspetuchovas.health.common;

import org.junit.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeProviderTest {
  @Test
  public void nowShouldReturnCurrentDate() {
    assertThat(new TimeProvider().now()).isCloseTo(new Date(), 1000);
  }
}
