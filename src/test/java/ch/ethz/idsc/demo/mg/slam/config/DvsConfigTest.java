// code by jph
package ch.ethz.idsc.demo.mg.slam.config;

import ch.ethz.idsc.retina.util.math.Magnitude;
import junit.framework.TestCase;

public class DvsConfigTest extends TestCase {
  public void testSimple() {
    DvsConfig davisConfig = SlamDvsConfig.eventCamera.slamCoreConfig.dvsConfig;
    int delta = Magnitude.MICRO_SECOND.toInt(davisConfig.filterConstant);
    assertTrue(200 <= delta && delta <= 5000);
  }
}
