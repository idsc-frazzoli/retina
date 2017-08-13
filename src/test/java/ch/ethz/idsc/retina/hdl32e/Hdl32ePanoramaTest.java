// code by jph
package ch.ethz.idsc.retina.hdl32e;

import ch.ethz.idsc.retina.hdl32e.img.Hdl32ePanorama;
import junit.framework.TestCase;

public class Hdl32ePanoramaTest extends TestCase {
  public void testSufficient() {
    assertTrue(2200 < Hdl32ePanorama.MAX_WIDTH);
  }
}
