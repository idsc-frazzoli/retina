// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pos;

public enum GokartPoseLcmServer {
  INSTANCE;
  // ---
  final OdometryLcmClient odometryLcmClient = new OdometryLcmClient();

  public GokartPoseOdometry getGokartPoseOdometry() {
    return odometryLcmClient.gokartPoseOdometry;
  }
}
