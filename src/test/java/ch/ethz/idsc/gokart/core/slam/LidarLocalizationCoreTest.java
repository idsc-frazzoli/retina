// code by jph
package ch.ethz.idsc.gokart.core.slam;

import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class LidarLocalizationCoreTest extends TestCase {
  public void testInitial() {
    LidarLocalizationCore lidarLocalizationCore = new LidarLocalizationCore(LocalizationConfig.GLOBAL);
    assertEquals(lidarLocalizationCore.getPose(), Tensors.fromString("{0[m], 0[m], 0}"));
    assertEquals(lidarLocalizationCore.getVelocity(), Tensors.fromString("{0[m*s^-1], 0[m*s^-1], 0[s^-1]}"));
    assertEquals(lidarLocalizationCore.getGyroZ(), Tensors.fromString("0[s^-1]"));
    assertEquals(lidarLocalizationCore.getGyroZ_vmu931(), Tensors.fromString("0[s^-1]"));
  }

  public void testResetPose() {
    LidarLocalizationCore lidarLocalizationCore = new LidarLocalizationCore(LocalizationConfig.GLOBAL);
    assertEquals(lidarLocalizationCore.getPose(), Tensors.fromString("{0[m], 0[m], 0}"));
    lidarLocalizationCore.resetPose(PoseHelper.attachUnits(Tensors.vector(1, 2, 3)));
    assertEquals(lidarLocalizationCore.getPose(), Tensors.fromString("{1[m], 2[m], 3}"));
    assertEquals(lidarLocalizationCore.getVelocity(), Tensors.fromString("{0[m*s^-1], 0[m*s^-1], 0[s^-1]}"));
  }

  public void testQuality() {
    LidarLocalizationCore lidarLocalizationCore = new LidarLocalizationCore(LocalizationConfig.GLOBAL);
    assertEquals(lidarLocalizationCore.quality, RealScalar.ZERO);
    lidarLocalizationCore.quality = RealScalar.ONE;
    lidarLocalizationCore.thread.start();
    assertEquals(lidarLocalizationCore.quality, RealScalar.ONE);
    lidarLocalizationCore.thread.interrupt();
    assertEquals(lidarLocalizationCore.quality, RealScalar.ONE);
    lidarLocalizationCore.lidarRayBlock(null);
    assertEquals(lidarLocalizationCore.quality, RealScalar.ZERO);
  }

  public void testQualityOk() {
    LidarLocalizationCore lidarLocalizationCore = new LidarLocalizationCore(LocalizationConfig.GLOBAL);
    lidarLocalizationCore.quality = RealScalar.ONE;
    lidarLocalizationCore.setTracking(true);
    lidarLocalizationCore.thread.start();
    assertEquals(lidarLocalizationCore.quality, RealScalar.ONE);
    lidarLocalizationCore.thread.interrupt();
    assertEquals(lidarLocalizationCore.quality, RealScalar.ONE);
  }
}
