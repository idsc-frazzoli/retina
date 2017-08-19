// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import ch.ethz.idsc.retina.lcm.davis.DavisSerial;
import junit.framework.TestCase;

public class DavisApsCorrectionTest extends TestCase {
  public void testSimple() {
    DavisApsCorrection davisApsCorrection = new DavisApsCorrection(DavisSerial.FX2_02460045.name());
    assertEquals(davisApsCorrection.pitchblack.length, 43200);
  }
}
