// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import junit.framework.TestCase;

public class LinmotCalibrationProviderTest extends TestCase {
  public void testSimple() {
    LinmotCalibrationProvider lcp = LinmotCalibrationProvider.INSTANCE;
    assertTrue(lcp.isIdle());
    assertFalse(lcp.putEvent().isPresent());
    lcp.schedule();
    assertTrue(lcp.putEvent().isPresent());
    assertFalse(lcp.isIdle());
  }
}
