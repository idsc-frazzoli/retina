// code by jph
package ch.ethz.idsc.retina.lidar.vlp16;

import java.util.Arrays;

import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import junit.framework.TestCase;

public class Vlp16SegmentProviderTest extends TestCase {
  public void testLowest() {
    double angle_offset = SensorsConfig.GLOBAL.vlp16_twist.number().doubleValue();
    Vlp16SegmentProvider vlp16SegmentProvider = new Vlp16SegmentProvider(angle_offset, -15);
    assertEquals(vlp16SegmentProvider.laserList(), Arrays.asList(0));
  }

  public void testLowestTwo() {
    double angle_offset = SensorsConfig.GLOBAL.vlp16_twist.number().doubleValue();
    Vlp16SegmentProvider vlp16SegmentProvider = new Vlp16SegmentProvider(angle_offset, -13);
    assertEquals(vlp16SegmentProvider.laserList(), Arrays.asList(0, 6));
    assertEquals(Vlp16Helper.lidarId(-15) * 3, 0);
    assertEquals(Vlp16Helper.lidarId(-13) * 3, 6);
  }
}
