// code by jph
package ch.ethz.idsc.demo.mg;

import ch.ethz.idsc.demo.mg.slam.config.DvsConfig;
import ch.ethz.idsc.demo.mg.slam.config.SlamDvsConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;
import junit.framework.TestCase;

public class DavisConfigTest extends TestCase {
  public void testSimple() {
    DvsConfig davisConfig = SlamDvsConfig.getDvsConfig();
    int delta = Magnitude.MICRO_SECOND.toInt(davisConfig.filterConstant);
    assertTrue(200 <= delta && delta <= 5000);
  }
}
