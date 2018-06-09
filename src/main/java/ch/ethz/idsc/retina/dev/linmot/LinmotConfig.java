// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import java.io.Serializable;

import ch.ethz.idsc.gokart.core.fuse.EmergencyBrakeManeuver;
import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;

/** parameters for brake configuration and emergency brake maneuver
 * 
 * see also document: 20180217_emergency_braking.pdf
 * https://www.youtube.com/watch?v=b_Sqy2TmKIk */
public class LinmotConfig implements Serializable {
  public static final LinmotConfig GLOBAL = AppResources.load(new LinmotConfig());
  /***************************************************/
  public Scalar windingTempCold = Quantity.of(5, NonSI.DEGREE_CELSIUS);
  public Scalar windingTempGlow = Quantity.of(85, NonSI.DEGREE_CELSIUS);
  public Scalar windingTempFire = Quantity.of(110, NonSI.DEGREE_CELSIUS);
  // ---
  /** minimum velocity required to trigger emergency brake */
  public Scalar minVelocity = Quantity.of(0.3, SI.VELOCITY);
  /** the response time is computed with the following rational
   * the lidar takes 0.05[s] == 20[Hz^-1] max to detect an obstacle
   * the brake requires 0.05[s] to move from home position to max press
   * at max brake press, the tires will lock after another 0.1[s].
   * to be conservative, we add 0.05[s] */
  public Scalar responseTime = Quantity.of(0.05 + 0.05 + 0.1 + 0.05, SI.SECOND);
  /** the analysis of log files has yielded a deceleration of -4.5[m*s^-2]
   * to be conservative, we assume a deceleration of -4.3[m*s^-2] */
  public Scalar maxDeceleration = Quantity.of(-4.3, SI.ACCELERATION);

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
