// code by mg
package ch.ethz.idsc.demo.mg;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmLidar;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

// should provide a global pose estimate based on wheel odometry through forward integration.
// idea: in SLAM applications, the estimate could then be fused with pose estimates from other sensors
// TODO find formulas to forward integrate gokart pose
public class WheelOdometryDemo implements OfflineLogListener {
  private final GokartPoseLcmLidar gokartPoseInterface = new GokartPoseLcmLidar();
  private Tensor estimatedPose; // pose estimated by wheel odometry
  private Tensor truePose; // pose provided by LIDAR

  WheelOdometryDemo() {
    gokartPoseInterface.gokartPoseLcmClient.startSubscriptions();
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(RimoLcmServer.CHANNEL_GET)) {
      RimoGetEvent rge = new RimoGetEvent(byteBuffer);
      Scalar speed = ChassisGeometry.GLOBAL.odometryTangentSpeed(rge);
      Scalar rate = ChassisGeometry.GLOBAL.odometryTurningRate(rge);
    }
    if (channel.equals(GokartLcmChannel.STATUS)) {
      GokartStatusEvent gse = new GokartStatusEvent(byteBuffer);
      if (gse.isSteerColumnCalibrated()) {
        Tensor steeringColumn = gse.getSteerColumnEncoderCentered().map(SteerPutEvent.ENCODER);
      }
    }
  }

  private void comparePose() {
    truePose = gokartPoseInterface.getPose();
  }

  // for testing
  public static void main(String[] args) {
    // ..
  }
}