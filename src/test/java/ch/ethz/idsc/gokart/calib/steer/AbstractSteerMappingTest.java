// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import junit.framework.TestCase;

public class AbstractSteerMappingTest extends TestCase {
  public void testNullFail() {
    try {
      new AbstractSteerMapping(null, null) {
        // ---
      };
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
