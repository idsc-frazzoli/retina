// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import junit.framework.TestCase;

public class Hdl32ePanoramaTest extends TestCase {
  public void testSimple() {
    assertEquals(4096, Hdl32ePanorama.MAX_WIDTH);
  }
}
