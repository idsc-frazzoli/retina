// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.gokart.core.perc.SimpleSpacialObstaclePredicate;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.math.SIDerived;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;

/**  */
public class TrackDetectionLidarConfig {
  public static final TrackDetectionLidarConfig GLOBAL = AppResources.load(new TrackDetectionLidarConfig());
  /***************************************************/
  /** obstacles on path within clearance range may cause
   * gokart to deactivate motor torque
   * 20171218: changed from 3.3[m] to 4.3[m]
   * 20180607: changed from 4.3[m] to 7.0[m]
   * 20180904: changed from 7.0[m] to 4.5[m]
   * @see Vlp16ClearanceModule */
  public Scalar clearance_XHi = Quantity.of(4.5, SI.METER);
  /** .
   * 20180226: changed from -1.0[m] to -0.90[m] because the sensor rack was lowered by ~8[cm]
   * 20181206: changed from -0.9[m] to -1.05[m] to detect a car tire flat on the ground as an obstacle */
  public Scalar vlp16_ZLo = Quantity.of(-1.05, SI.METER);
  /** 20181206: changed from +0.1[m] to -0.1[m] because artifacts were observed when driving fast
   * cause probably by the arrangement of the lidar on the sensor rack
   * TODO investigate and quantify artifacts! */
  public Scalar vlp16_ZHi = Quantity.of(-0.10, SI.METER);
  /** rate limit is used in {@link SpeedLimitSafetyModule} */
  public Scalar rateLimit = Quantity.of(30, SIDerived.RADIAN_PER_SECOND);
  public final Scalar penalty = Quantity.of(0.5, SI.SECOND);

  /***************************************************/
  /** @return */
  public Clip vlp16_ZClip() {
    return Clip.function( //
        Magnitude.METER.apply(vlp16_ZLo), //
        Magnitude.METER.apply(vlp16_ZHi));
  }

  /** convenient way for the application layer to obtain an instance
   * without having to specify the geometric configuration
   * 
   * @return */
  public SpacialXZObstaclePredicate createSpacialXZObstaclePredicate() {
    return new SimpleSpacialObstaclePredicate(vlp16_ZClip(), SensorsConfig.GLOBAL.vlp16_incline);
  }
}
