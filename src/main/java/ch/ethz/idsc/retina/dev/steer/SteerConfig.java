// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.io.Serializable;

import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.UnitSystem;
import ch.ethz.idsc.tensor.sca.Clip;

/** parameters for PID controller of steering
 * 
 * there are 2 special units related to the manufacturer of the steering column:
 * "SCE" steer-column encoder
 * "SCT" steer-column torque */
public class SteerConfig implements Serializable {
  public static final SteerConfig GLOBAL = AppResources.load(new SteerConfig());
  /***************************************************/
  public Scalar voltageLo = Quantity.of(10.8, SI.VOLT); // 10.8[V] for 1[s] confirmed with mac
  public Scalar voltageHi = Quantity.of(13.0, SI.VOLT);
  // ---
  public Scalar calibration = Quantity.of(1.5, "SCT");
  public Scalar Ki = Quantity.of(1.95, "SCE^-1*SCT*s^-1");
  public Scalar Kp = Quantity.of(3.53, "SCE^-1*SCT");
  public Scalar Kd = Quantity.of(0.57, "SCE^-1*SCT*s");
  public Scalar torqueLimit = Quantity.of(1.5, "SCT");
  // ---
  /** maximum steer column value commanded by joystick or autonomous drive
   * originally, the value was close to the max possible: 0.6743167638778687[SCE]
   * but this choice put unnecessary stress on the hardware. */
  public Scalar columnMax = Quantity.of(0.6, SteerPutEvent.UNIT_ENCODER);
  /** conversion factor from measured steer column angle to front wheel angle */
  public Scalar column2steer = Quantity.of(0.6, "rad*SCE^-1");
  /** 0.5 corresponds to 50% of torque limit */
  public Scalar stepOfLimit = RealScalar.of(0.5);

  /***************************************************/
  /** @return voltage operating range of battery */
  public Clip operatingVoltageClip() {
    return Clip.function(voltageLo, voltageHi);
  }

  /** @return symmetric interval centered at zero that bounds the torque
   * applied to the steering wheel */
  public Clip torqueLimitClip() {
    return Clip.function(torqueLimit.negate(), torqueLimit);
  }

  /** @return scalar without unit but with interpretation in radians,
   * @throws Exception if {@link SteerColumnInterface#isSteerColumnCalibrated()} returns false */
  public Scalar getAngleFromSCE(SteerColumnInterface steerColumnInterface) {
    return UnitSystem.SI().apply( //
        steerColumnInterface.getSteerColumnEncoderCentered().multiply(column2steer));
  }

  public Scalar getSCEfromAngle(Scalar angle) {
    return angle.divide(column2steer);
  }

  public Clip getAngleLimit() {
    Scalar angleMax = columnMax.multiply(column2steer);
    return Clip.function(angleMax.negate(), angleMax);
  }
}
