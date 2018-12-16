// code by jph
package ch.ethz.idsc.gokart.dev.linmot;

import java.util.Optional;

import junit.framework.TestCase;

public class LinmotPutFallbackTest extends TestCase {
  public void testOperational() {
    Optional<LinmotPutEvent> optional = LinmotPutFallback.INSTANCE.putEvent();
    assertTrue(optional.isPresent());
    LinmotPutEvent linmotPutEvent = optional.get();
    assertTrue(linmotPutEvent.isOperational());
    assertEquals(linmotPutEvent.target_position, -50);
    assertEquals(linmotPutEvent.max_velocity, 1000);
    assertEquals(linmotPutEvent.acceleration, 500);
    assertEquals(linmotPutEvent.deceleration, 500);
  }
}
