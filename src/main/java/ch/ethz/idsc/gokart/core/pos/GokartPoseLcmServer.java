// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.BinaryBlobPublisher;

/** owner of odometry tracker instance that is corrected by lidar
 * {@link GokartPoseLcmModule} */
public enum GokartPoseLcmServer {
  INSTANCE;
  // ---
  final BinaryBlobPublisher binaryBlobPublisher = new BinaryBlobPublisher(GokartLcmChannel.POSE_LIDAR);
  // TODO in need of documentation
  final OdometryRimoGetLcmClient odometryRimoGetLcmClient = new OdometryRimoGetLcmClient();

  public GokartPoseOdometry getGokartPoseOdometry() {
    return odometryRimoGetLcmClient.gokartPoseOdometry;
  }

  public void publish() {
    publish(odometryRimoGetLcmClient.gokartPoseOdometry.getPoseEvent());
  }

  public void publish(GokartPoseEvent gokartPoseEvent) {
    binaryBlobPublisher.accept(gokartPoseEvent.asArray());
  }
}
