// code by jph
package ch.ethz.idsc.retina.lcm.lidar;

import ch.ethz.idsc.retina.dev.lidar.VelodyneModel;
import junit.framework.TestCase;

public class VelodyneLcmChannelsTest extends TestCase {
  public void testRay() {
    String channel = VelodyneLcmChannels.ray(VelodyneModel.VLP16, "center");
    assertEquals(channel, "vlp16.center.ray");
  }

  public void testPos() {
    String channel = VelodyneLcmChannels.pos(VelodyneModel.VLP16, "center");
    assertEquals(channel, "vlp16.center.pos");
  }
}
