// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.io.Serializable;

import ch.ethz.idsc.owl.car.math.DifferentialSpeed;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.ArcTan;

public class ChassisGeometry implements Serializable {
  public static final ChassisGeometry GLOBAL = AppResources.load(new ChassisGeometry());
  /***************************************************/
  /** distance from rear to front axle [m] */
  public Scalar xAxleRtoF = Quantity.of(+1.19, SI.METER);
  public Scalar xAxleFtoTip = Quantity.of(+0.56, SI.METER);
  /** from center to outer protection boundary */
  public Scalar yHalfWidth = Quantity.of(0.7, SI.METER);
  /** distance from x-axis to front tire */
  public Scalar yTireFront = Quantity.of(0.48, SI.METER);
  /** distance from x-axis to rear tire */
  public Scalar yTireRear = Quantity.of(0.54, SI.METER);
  /** approx. radius of tire when on gokart is on ground */
  public Scalar tireRadiusFront = Quantity.of(0.23 * 0.5, "m*rad^-1");
  public Scalar tireRadiusRear = Quantity.of(0.240 * 0.5, "m*rad^-1");
  /** front tire half width */
  public Scalar tireHalfWidthFront = Quantity.of(0.065, SI.METER);
  public Scalar tireHalfWidthContactFront = Quantity.of(0.045, SI.METER);
  /** rear tire half width */
  public Scalar tireHalfWidthRear = Quantity.of(0.0975, SI.METER);
  public Scalar tireHalfWidthContactRear = Quantity.of(0.0675, SI.METER);
  /** approximation of ground clearance measured on 20180507 */
  public Scalar groundClearance = Quantity.of(0.03, SI.METER);

  /***************************************************/
  public Scalar yHalfWidthMeter() {
    return Magnitude.METER.apply(yHalfWidth);
  }

  /** @return approximately 1.19 */
  public Scalar xAxleDistanceMeter() {
    return Magnitude.METER.apply(xAxleRtoF);
  }

  public Scalar xTipMeter() {
    return Total.of(Tensors.of(xAxleRtoF, xAxleFtoTip).map(Magnitude.METER)).Get();
  }

  public Scalar yTireRearMeter() {
    return Magnitude.METER.apply(yTireRear);
  }

  public Scalar yTireFrontMeter() {
    return Magnitude.METER.apply(yTireFront);
  }

  public Scalar tireHalfWidthFront() {
    return Magnitude.METER.apply(tireHalfWidthFront);
  }

  public Scalar tireHalfWidthRear() {
    return Magnitude.METER.apply(tireHalfWidthRear);
  }

  public DifferentialSpeed getDifferentialSpeed() {
    return DifferentialSpeed.fromSI(xAxleDistanceMeter(), yTireRearMeter());
  }

  /** function ArcTan[d * r] approx. d * r for d ~ 1 and small r
   * 
   * @param ratio without unit */
  public Scalar steerAngleForTurningRatio(Scalar ratio) {
    return Quantity.of(ArcTan.of(xAxleDistanceMeter().multiply(ratio)), "rad");
  }

  /** @param rimoGetEvent
   * @return velocity of the gokart projected to the x-axis in unit "m*s^-1"
   * computed from the angular rates of the rear wheels. The odometry value
   * has error due to slip. */
  public Scalar odometryTangentSpeed(RimoGetEvent rimoGetEvent) {
    return odometryTangentSpeed(rimoGetEvent.getAngularRate_Y_pair());
  }

  public Scalar odometryTangentSpeed(Tensor angularRate_Y_pair) {
    return Mean.of(angularRate_Y_pair).multiply(tireRadiusRear).Get();
  }

  /** @param rimoGetEvent
   * @return rotational rate of the gokart (around z-axis) in unit "s^-1"
   * computed from the angular rates of the rear wheels. The odometry value
   * has error due to slip. */
  public Scalar odometryTurningRate(RimoGetEvent rimoGetEvent) {
    return odometryTangentSpeed(rimoGetEvent.getAngularRate_Y_pair());
  }

  public Scalar odometryTurningRate(Tensor angularRate_Y_pair) {
    // rad/s * m == (m / s) / m
    return Differences.of(angularRate_Y_pair).Get(0) //
        .multiply(RationalScalar.HALF).multiply(tireRadiusRear).divide(yTireRear);
  }

  public Tensor odometryVelocity(Tensor angularRate_Y_pair) {
    return Tensors.of( //
        odometryTangentSpeed(angularRate_Y_pair), //
        Quantity.of(0, SI.VELOCITY), //
        odometryTurningRate(angularRate_Y_pair));
  }
}
