// code by jph
package ch.ethz.idsc.retina.lidar.vlp16;

import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testDegToInd() {
    for (int laserId = 0; laserId < 16; ++laserId) {
      int degree = StaticHelper.degree(laserId);
      int toLidarId = StaticHelper.lidarId(degree);
      assertEquals(laserId, toLidarId);
    }
  }
}
