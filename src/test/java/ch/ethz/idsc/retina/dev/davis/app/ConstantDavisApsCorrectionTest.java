// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import ch.ethz.idsc.retina.demo.DavisSerial;
import junit.framework.TestCase;

public class ConstantDavisApsCorrectionTest extends TestCase {
  public void testSimple() {
    DavisApsCorrection davisApsCorrection = new ConstantDavisApsCorrection(DavisSerial.FX2_02460045.name());
    assertEquals(davisApsCorrection.pitchblack.length, 43200);
  }
}
