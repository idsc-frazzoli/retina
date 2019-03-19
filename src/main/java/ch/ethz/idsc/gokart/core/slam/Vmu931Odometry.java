// code by mh, jph
package ch.ethz.idsc.gokart.core.slam;

import ch.ethz.idsc.gokart.calib.vmu931.PlanarVmu931Imu;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrameListener;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

public class Vmu931Odometry implements Vmu931ImuFrameListener {
  private static final Clip VMU931_CLIP_TIME = Clips.interval(Quantity.of(0, SI.SECOND), Quantity.of(0.01, SI.SECOND));
  // ---
  private final PlanarVmu931Imu planarVmu931Imu;
  public final InertialOdometry inertialOdometry = new InertialOdometry();
  private int vmu931_timestamp_ms = 0;

  public Vmu931Odometry(PlanarVmu931Imu planarVmu931Imu) {
    this.planarVmu931Imu = planarVmu931Imu;
  }

  @Override // from Vmu931ImuFrameListener
  public void vmu931ImuFrame(Vmu931ImuFrame vmu931ImuFrame) {
    Scalar deltaT = VMU931_CLIP_TIME.apply(Quantity.of((vmu931ImuFrame.timestamp_ms() - vmu931_timestamp_ms) * 1e-3, SI.SECOND));
    vmu931_timestamp_ms = vmu931ImuFrame.timestamp_ms();
    inertialOdometry.integrateImu( //
        planarVmu931Imu.vmu931AccXY(vmu931ImuFrame), //
        planarVmu931Imu.vmu931GyroZ(vmu931ImuFrame), //
        deltaT);
  }
}
