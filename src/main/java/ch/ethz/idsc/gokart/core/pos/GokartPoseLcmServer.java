// code by jph
package ch.ethz.idsc.gokart.core.pos;

/** owner of odometry tracker instance that is corrected by lidar */
public enum GokartPoseLcmServer {
  INSTANCE;
  // ---
  final OdometryRimoGetLcmClient odometryRimoGetLcmClient = new OdometryRimoGetLcmClient();

  public GokartPoseOdometry getGokartPoseOdometry() {
    return odometryRimoGetLcmClient.gokartPoseOdometry;
  }
}
