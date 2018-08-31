// code by jph
package ch.ethz.idsc.demo.mg;

import ch.ethz.idsc.retina.util.math.Magnitude;
import junit.framework.TestCase;

public class DavisConfigTest extends TestCase {
  public void testSimple() {
    DavisConfig davisConfig = new DavisConfig();
    int delta = Magnitude.MICRO_SECOND.toInt(davisConfig.filterConstant);
    assertTrue(200 <= delta && delta <= 5000);
  }
}
