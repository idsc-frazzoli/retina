// code by jph
package ch.ethz.idsc.gokart.core.slam;

import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class LidarLocalizationCoreTest extends TestCase {
  public void testInitial() {
    LidarLocalizationCore lidarLocalizationCore = new LidarLocalizationCore();
    assertEquals(lidarLocalizationCore.getPose(), Tensors.fromString("{0[m],0[m],0}"));
    assertEquals(lidarLocalizationCore.getVelocity(), Tensors.fromString("{0[m*s^-1],0[m*s^-1],0[s^-1]}"));
    assertEquals(lidarLocalizationCore.getVelocityXY(), Tensors.fromString("{0[m*s^-1],0[m*s^-1]}"));
    assertEquals(lidarLocalizationCore.getGyroZ(), Tensors.fromString("0[s^-1]"));
    assertEquals(lidarLocalizationCore.getGyroZ_vmu931(), Tensors.fromString("0[s^-1]"));
  }
}
