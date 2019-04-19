// code by jph
package ch.ethz.idsc.retina.lidar.vlp16;

import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testDegToInd() {
    for (int laserId = 0; laserId < 16; ++laserId) {
      int degree = Vlp16Helper.degree(laserId);
      int toLidarId = Vlp16Helper.lidarId(degree);
      assertEquals(laserId, toLidarId);
    }
  }
}
