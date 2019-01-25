// code by jph
package ch.ethz.idsc.gokart.core.ekf;

import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmModule;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.BinaryBlobPublisher;

/** owner of odometry tracker instance that is corrected by lidar
 * {@link GokartPoseLcmModule} */
public enum GokartVelocityLcmServer {
  INSTANCE;
  // ---
  final BinaryBlobPublisher binaryBlobPublisher = new BinaryBlobPublisher(GokartLcmChannel.VELOCITY_FUSION);

  public void publish() {
    // TODO: maybe move that to other place
    //GokartVelocityEvent gokartVelocityEvent = GokartVelocityEvents.getPoseEvent(velocity)
    //publish(odometryRimoGetLcmClient.gokartPoseOdometry.getPoseEvent());
  }

  public void publish(GokartVelocityEvent gokartVelocityEvent) {
    binaryBlobPublisher.accept(gokartVelocityEvent.asArray());
  }
}
