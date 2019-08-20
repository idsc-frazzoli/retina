// code by mh, jph
package ch.ethz.idsc.gokart.core.slam;

import java.util.Objects;

import ch.ethz.idsc.gokart.calib.vmu931.PlanarVmu931Imu;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrameListener;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/** implementation feeds the values provided by the VMU931 sensor
 * into the inertial odometry */
public final class Vmu931Odometry extends InertialOdometry implements Vmu931ImuFrameListener {
  private static final Clip VMU931_CLIP_TIME = Clips.positive(Quantity.of(0.01, SI.SECOND));
  // ---
  private final PlanarVmu931Imu planarVmu931Imu;
  /** the initial value 0 is a systematic error.
   * the correct initialization would be the timestamp of the first message
   * however, the use of VMU931_CLIP_TIME bounds the integration step size to 0.01[s] */
  private int vmu931_timestamp_ms = 0;

  public Vmu931Odometry(PlanarVmu931Imu planarVmu931Imu) {
    this.planarVmu931Imu = Objects.requireNonNull(planarVmu931Imu);
  }

  @Override // from Vmu931ImuFrameListener
  public void vmu931ImuFrame(Vmu931ImuFrame vmu931ImuFrame) {
    int delta_ms = vmu931ImuFrame.timestamp_ms() - vmu931_timestamp_ms;
    vmu931_timestamp_ms = vmu931ImuFrame.timestamp_ms(); // store for next
    integrateImu( //
        planarVmu931Imu.accXY(vmu931ImuFrame), //
        planarVmu931Imu.gyroZ(vmu931ImuFrame), //
        VMU931_CLIP_TIME.apply(Quantity.of(delta_ms * 1e-3, SI.SECOND)));
  }
}
