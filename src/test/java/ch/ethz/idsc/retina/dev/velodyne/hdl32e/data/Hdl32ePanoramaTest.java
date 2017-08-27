// code by jph
package ch.ethz.idsc.retina.dev.velodyne.hdl32e.data;

import junit.framework.TestCase;

public class Hdl32ePanoramaTest extends TestCase {
  public void testSufficient() {
    assertTrue(2200 < Hdl32ePanorama.MAX_WIDTH);
  }
}
