// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.gokart.core.perc.SimpleSpacialObstaclePredicate;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.retina.app.clear.CircleClearanceTracker;
import ch.ethz.idsc.retina.app.clear.ClearanceTracker;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/**  */
public class SafetyConfig {
  public static final SafetyConfig GLOBAL = AppResources.load(new SafetyConfig());
  /***************************************************/
  public Scalar clearance_XLo = Quantity.of(0.2, SI.SECOND);
  /** obstacles on path within clearance range may cause
   * gokart to deactivate motor torque
   * 20171218: changed from 3.3[m] to 4.3[m]
   * 20180607: changed from 4.3[m] to 7.0[m]
   * 20180904: changed from 7.0[m] to 4.5[m]
   * @see Vlp16ClearanceModule */
  public Scalar clearance_XHi = Quantity.of(4.5, SI.SECOND);
  /** 20180226: changed from -1.0[m] to -0.9[m] because the sensor rack was lowered by ~8[cm] */
  public Scalar vlp16_ZLo = Quantity.of(-1.05, SI.METER);
  public Scalar vlp16_ZHi = Quantity.of(+0.1, SI.METER);
  /** rate limit is used in {@link SpeedLimitSafetyModule} */
  public Scalar rateLimit = Quantity.of(30, SI.PER_SECOND);
  public final Scalar penalty = Quantity.of(0.5, SI.SECOND);
  /** {@link LocalizationEmergencyModule} */
  public Boolean checkPoseQuality = true;
  /** {@link AutonomousSafetyModule} */
  public Boolean checkAutonomy = true;

  /***************************************************/
  /** @return */
  /* package */ Clip vlp16_ZClip() {
    return Clips.interval( //
        Magnitude.METER.apply(vlp16_ZLo), //
        Magnitude.METER.apply(vlp16_ZHi));
  }

  /** @return */
  /* package */ Clip getClearanceClip() {
    return Clips.interval( //
        Magnitude.SECOND.apply(clearance_XLo), //
        Magnitude.SECOND.apply(clearance_XHi));
  }

  /** @param speed
   * @param ratio non-null
   * @return */
  public ClearanceTracker getClearanceTracker(Scalar speed, Scalar ratio) {
    Scalar half = ChassisGeometry.GLOBAL.yHalfWidthMeter();
    return new CircleClearanceTracker( //
        speed, half, ratio, //
        PoseHelper.toUnitless(SensorsConfig.GLOBAL.vlp16_pose), getClearanceClip());
  }

  /** convenient way for the application layer to obtain an instance
   * without having to specify the geometric configuration
   * 
   * @return predicate to perform obstacle checking */
  public SpacialXZObstaclePredicate createSpacialXZObstaclePredicate() {
    return new SimpleSpacialObstaclePredicate(vlp16_ZClip(), SensorsConfig.GLOBAL.vlp16_incline);
  }
}
