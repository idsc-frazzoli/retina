// code by jph
package ch.ethz.idsc.gokart.dev.steer;

import junit.framework.TestCase;

public class SteerGetEventsTest extends TestCase {
  public void testSimple() {
    assertFalse(SteerGetEvents.ZEROS.isActive());
  }
}
