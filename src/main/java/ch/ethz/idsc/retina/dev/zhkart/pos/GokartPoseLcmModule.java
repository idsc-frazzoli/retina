// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pos;

import ch.ethz.idsc.retina.gui.gokart.GokartLcmChannel;
import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.retina.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Tensor;

public class GokartPoseLcmModule extends AbstractClockedModule {
  private static final double PERIOD_S = 0.02; // 1/0.02[s] == 50[Hz]
  // ---
  private final BinaryBlobPublisher binaryBlobPublisher = new BinaryBlobPublisher(GokartLcmChannel.POSE_LIDAR);

  @Override // from AbstractModule
  protected void first() throws Exception {
    GokartPoseLcmServer.INSTANCE.odometryLcmClient.startSubscriptions();
  }

  @Override // from AbstractModule
  protected void last() {
    GokartPoseLcmServer.INSTANCE.odometryLcmClient.stopSubscriptions();
  }

  @Override // from AbstractClockedModule
  protected void runAlgo() {
    Tensor pose = GokartPoseLcmServer.INSTANCE.odometryLcmClient.gokartPoseOdometry.getPose();
    GokartPoseEvent gokartPoseEvent = new GokartPoseEvent(pose);
    binaryBlobPublisher.accept(gokartPoseEvent.asArray());
  }

  @Override // from AbstractClockedModule
  protected double getPeriod() {
    return PERIOD_S;
  }
}
