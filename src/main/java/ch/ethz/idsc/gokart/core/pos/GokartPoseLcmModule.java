// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.retina.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/**  */
public class GokartPoseLcmModule extends AbstractClockedModule {
  private static final Scalar PERIOD = Quantity.of(50, "Hz").reciprocal();
  // ---
  private final BinaryBlobPublisher binaryBlobPublisher = new BinaryBlobPublisher(GokartLcmChannel.POSE_LIDAR);

  @Override // from AbstractModule
  protected void first() throws Exception {
    GokartPoseLcmServer.INSTANCE.odometryRimoGetLcmClient.startSubscriptions();
  }

  @Override // from AbstractModule
  protected void last() {
    GokartPoseLcmServer.INSTANCE.odometryRimoGetLcmClient.stopSubscriptions();
  }

  @Override // from AbstractClockedModule
  protected void runAlgo() {
    // TODO the pose server publishes pose values even when the pose is not initialized (in that case the quality == 0)
    GokartPoseEvent gokartPoseEvent = GokartPoseLcmServer.INSTANCE.odometryRimoGetLcmClient.gokartPoseOdometry.getPoseEvent();
    binaryBlobPublisher.accept(gokartPoseEvent.asArray());
  }

  @Override // from AbstractClockedModule
  protected Scalar getPeriod() {
    return PERIOD;
  }
}
