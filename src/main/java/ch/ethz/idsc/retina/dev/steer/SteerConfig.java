// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.io.Serializable;

import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.UnitSystem;
import ch.ethz.idsc.tensor.red.Times;
import ch.ethz.idsc.tensor.sca.Clip;

/** parameters for PID controller of steering
 * 
 * there are 2 special units related to the manufacturer of the steering column:
 * "SCE" steer-column encoder
 * "SCT" steer-column torque */
public class SteerConfig implements Serializable {
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
  public Scalar voltageLo = Quantity.of(3.6 * 3, SI.VOLT); // 3.6 * 3 == 10.8[V]
  public Scalar voltageHi = Quantity.of(4.2 * 3, SI.VOLT); // 4.2 * 3 == 12.9[V]
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
  /** conversion factor from measured steer column angle to front wheel angle */
  public Scalar column2steer = Quantity.of(0.6, "rad*SCE^-1");
  /** linear factor for advanced Steering function */
  public Scalar advColumn2steer1 = Quantity.of(0.93, "rad*SCE^-1");
  /** cubic factor for advanced Steering function */
  public Scalar advColumn2steer3 = Quantity.of(-0.58, "rad*SCE^-3");
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

  /** @return scalar without unit but with interpretation in radians
   * @throws Exception if {@link SteerColumnInterface#isSteerColumnCalibrated()} returns false */
  public Scalar getAdvAngleFromSCE(SteerColumnInterface steerColumnInterface) {
    Scalar CSEangle = steerColumnInterface.getSteerColumnEncoderCentered();
    Scalar linearComponent = Times.of(CSEangle, advColumn2steer1);
    Scalar cubicComponent = Times.of(CSEangle,CSEangle, CSEangle, advColumn2steer3);
    return UnitSystem.SI().apply(
        linearComponent.add(cubicComponent));
  }

  /** @return scalar without unit but with interpretation in radians
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
