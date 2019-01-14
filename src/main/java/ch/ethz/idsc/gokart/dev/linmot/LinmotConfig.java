// code by jph
package ch.ethz.idsc.gokart.dev.linmot;

import ch.ethz.idsc.gokart.core.fuse.EmergencyBrakeManeuver;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;

/** parameters for brake configuration and emergency brake maneuver
 * 
 * see also document: 20180217_emergency_braking.pdf
 * https://www.youtube.com/watch?v=b_Sqy2TmKIk */
public class LinmotConfig {
  public static final LinmotConfig GLOBAL = AppResources.load(new LinmotConfig());
  /***************************************************/
  public final Scalar windingTempCold = Quantity.of(5, NonSI.DEGREE_CELSIUS);
  public final Scalar windingTempGlow = Quantity.of(85, NonSI.DEGREE_CELSIUS);
  public final Scalar windingTempFire = Quantity.of(110, NonSI.DEGREE_CELSIUS);
  // ---
  /** minimum velocity required to trigger emergency brake */
  public Scalar minVelocity = Quantity.of(0.3, SI.VELOCITY);
  /** the response time is computed with the following rational
   * the lidar takes 0.05[s] == 20[Hz^-1] max to detect an obstacle
   * the brake requires 0.05[s] to move from home position to max press
   * at max brake press, the tires will lock after another 0.1[s].
   * to be conservative, we add 0.05[s] */
  public final Scalar responseTime = Quantity.of(0.05 + 0.05 + 0.1 + 0.05, SI.SECOND);
  /** the analysis of log files has yielded a deceleration of -4.5[m*s^-2]
   * to be conservative, we assume a deceleration of -4.3[m*s^-2] */
  public final Scalar maxDeceleration = Quantity.of(-4.3, SI.ACCELERATION);
  /** steps defines the number of levels at which to test the brake */
  public Scalar pressTestSteps = RealScalar.of(20);
  /** duration used in LinmotPressModule for calibration purpose */
  public Scalar pressTestDuration = Quantity.of(2.5, SI.SECOND);

  /***************************************************/
  public Clip temperatureOperationClip() {
    return Clip.function(windingTempCold, windingTempGlow);
  }

  public Clip temperatureHardwareClip() {
    return Clip.function(windingTempCold, windingTempFire);
  }

  public boolean isTemperatureOperationSafe(Scalar temperature) {
    return temperatureOperationClip().isInside(temperature);
  }

  public boolean isTemperatureHardwareSafe(Scalar temperature) {
    return temperatureHardwareClip().isInside(temperature);
  }

  /** @param velocity
   * @return conservative estimation of brake distance */
  public EmergencyBrakeManeuver brakeDistance(Scalar velocity) {
    return new EmergencyBrakeManeuver(responseTime, maxDeceleration, velocity);
  }

  /** bounds established using experimentation */
  // TODO make 20000 configurable
  public static final Clip NOMINAL_POSITION_DELTA = Clip.function(-20000, 20000);
}
