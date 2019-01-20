// code by jph
package ch.ethz.idsc.gokart.dev.steer;

import ch.ethz.idsc.gokart.calib.steer.CubicSteerMapping;
import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.math.SIDerived;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;

/** parameters for PID controller of steering
 * 
 * there are 2 special units related to the manufacturer of the steering column:
 * "SCE" steer-column encoder
 * "SCT" steer-column torque */
public class SteerConfig {
  public static final SteerConfig GLOBAL = AppResources.load(new SteerConfig());
  /***************************************************/
  /** battery supply voltage ratings
   * 
   * ante 20180628: the steer battery was
   * Yuasa NP2.3-12 with 2Ah, lead acid, 6 cells
   * the range was 10.8[V] .. 13.0[V] for 1[s] confirmed with mac
   * 
   * post 20180628: the steer battery is replaced. the new model is
   * Lithium Polymer Akku: 45C/90C 4500mAh 11.1V 3S1P */
  public final Scalar voltageLo = Quantity.of(3.6 * 3, SI.VOLT); // 3.6 * 3 == 10.8[V]
  public final Scalar voltageHi = Quantity.of(4.2 * 3, SI.VOLT); // 4.2 * 3 == 12.9[V]
  // ---
  /** amplitude of signal during calibration procedure */
  public Scalar calibration = Quantity.of(1.5, "SCT");
  public Scalar Ki = Quantity.of(1.95, "SCE^-1*SCT*s^-1");
  public Scalar Kp = Quantity.of(3.53, "SCE^-1*SCT");
  public Scalar Kd = Quantity.of(0.57, "SCE^-1*SCT*s");
  public Scalar torqueLimit = Quantity.of(1.5, "SCT");
  // ---
  /** maximum steer column value commanded by joystick or autonomous drive
   * originally, the value was close to the max possible: 0.6743167638778687[SCE]
   * but this choice put unnecessary stress on the hardware.
   * 
   * 20180517 the */
  // TODO JPH value of columnMax does not correspond to the above comment
  public Scalar columnMax = Quantity.of(0.7, SteerPutEvent.UNIT_ENCODER);
  /** 0.5 corresponds to 50% of torque limit */
  public Scalar stepOfLimit = RealScalar.of(0.5);
  /** max turning rate per meter
   * the numeric value was determined in an experiment documented in the report below
   * https://github.com/idsc-frazzoli/retina/files/1958724/20180429_minimum_turning_radius.pdf
   * The reciprocal gives the minimum turning radius to be approx. 2.45[m]. */
  public Scalar turningRatioMax = Quantity.of(0.4082, "rad*m^-1");

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

  /***************************************************/
  /** @return default steer mapping */
  public SteerMapping getSteerMapping() {
    // return CubicSteerMapping.approximation_1();
    // TODO once cubic mapping is confirmed, replace default implementation with cubic
    // return LinearSteerMapping.instance();
    return CubicSteerMapping.approximation_1();
  }

  /** @return */
  public Clip getAngleLimit() {
    Scalar angleMax = Quantity.of(getSteerMapping().getAngleFromSCE(columnMax), SIDerived.RADIAN);
    return Clip.function(angleMax.negate(), angleMax);
  }
}
