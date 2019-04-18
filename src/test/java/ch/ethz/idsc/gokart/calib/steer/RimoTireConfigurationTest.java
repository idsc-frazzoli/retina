// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import junit.framework.TestCase;

public class RimoTireConfigurationTest extends TestCase {
  public void testSimple() {
    assertEquals(RimoTireConfiguration.FRONT.footprint().length(), 4);
    assertEquals(RimoTireConfiguration._REAR.footprint().length(), 4);
  }
}
