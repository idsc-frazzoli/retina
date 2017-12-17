// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pos;

/** owner of odometry tracker instance that is corrected by lidar */
public enum GokartPoseLcmServer {
  INSTANCE;
  // ---
  final OdometryRimoGetLcmClient odometryLcmClient = new OdometryRimoGetLcmClient();

  public GokartPoseOdometry getGokartPoseOdometry() {
    return odometryLcmClient.gokartPoseOdometry;
  }
}
