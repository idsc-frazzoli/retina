// code by jph
package ch.ethz.idsc.gokart.gui.top;

import ch.ethz.idsc.gokart.calib.steer.RimoTireConfiguration;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.owl.car.math.AckermannSteering;
import ch.ethz.idsc.owl.car.math.DifferentialSpeed;
import ch.ethz.idsc.owl.car.math.TurningGeometry;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.UnitSystem;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.ArcTan;

/** parameters in this config class are final because they
 * correspond to immutable characteristic of the gokart. */
// TODO JPH location of class not good
public class ChassisGeometry {
  public static final ChassisGeometry GLOBAL = AppResources.load(new ChassisGeometry());
  /***************************************************/
  /** distance from rear to front axle [m] */
  public final Scalar xAxleRtoF = Quantity.of(+1.19, SI.METER);
  /** distance from front axle to front tip of gokart [m] */
  public final Scalar xAxleFtoTip = Quantity.of(+0.56, SI.METER);
  /** from center to outer protection boundary along y-axis */
  public final Scalar yHalfWidth = Quantity.of(0.7, SI.METER);
  /** distance from x-axis to front tire */
  public final Scalar yTireFront = Quantity.of(0.48, SI.METER);
  /** distance from x-axis to rear tire */
  public final Scalar yTireRear = Quantity.of(0.54, SI.METER);
  /** front tire half width contact */
  public final Scalar tireHalfWidthContactFront = Quantity.of(0.045, SI.METER);
  /** rear tire half width contact */
  public final Scalar tireHalfWidthContactRear = Quantity.of(0.0675, SI.METER);
  /** approximation of ground clearance measured on 20180507 */
  public final Scalar groundClearance = Quantity.of(0.03, SI.METER);
  /** approx. radius of front tire when on gokart is on ground [m/rad] */
  /** approx. radius of rear tire when on gokart is on ground [m/rad] */
  /** longitudinal distance to center of mass from back axle */
  public final Scalar xAxleRtoCoM = Quantity.of(0.46, SI.METER);

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

  public DifferentialSpeed getDifferentialSpeed() {
    return DifferentialSpeed.fromSI(xAxleDistanceMeter(), yTireRearMeter());
  }

  public AckermannSteering getAckermannSteering() {
    return new AckermannSteering(xAxleRtoF, yTireFront);
  }

  /** function ArcTan[d * r] approx. d * r for d ~ 1 and small r
   * inverse function of {@link TurningGeometry}
   * @param ratio without unit but with interpretation "rad*m^-1"
   * see for instance SteerConfig.GLOBAL.turningRatioMax
   * @return steering angle unitless */
  public Scalar steerAngleForTurningRatio(Scalar ratio) {
    // TODO JPH require ratio to have unit "rad*m^-1"
    return ArcTan.of(xAxleDistanceMeter().multiply(ratio));
  }

  /** @param rimoGetEvent
   * @return velocity of the gokart projected to the x-axis in unit "m*s^-1"
   * computed from the angular rates of the rear wheels. The odometry value
   * has error due to slip. */
  public Scalar odometryTangentSpeed(RimoGetEvent rimoGetEvent) {
    return odometryTangentSpeed(rimoGetEvent.getAngularRate_Y_pair());
  }

  public Scalar odometryTangentSpeed(Tensor angularRate_Y_pair) {
    return UnitSystem.SI().apply(Mean.of(angularRate_Y_pair).multiply(RimoTireConfiguration._REAR.radius()).Get());
  }

  /** @param rimoGetEvent
   * @return rotational rate of the gokart (around z-axis) in unit "s^-1"
   * computed from the angular rates of the rear wheels. The odometry value
   * has error due to slip. */
  public Scalar odometryTurningRate(RimoGetEvent rimoGetEvent) {
    return odometryTurningRate(rimoGetEvent.getAngularRate_Y_pair());
  }

  public Scalar odometryTurningRate(Tensor angularRate_Y_pair) {
    // rad/s * m == (m / s) / m
    return UnitSystem.SI().apply(Differences.of(angularRate_Y_pair).Get(0) //
        .multiply(RationalScalar.HALF).multiply(RimoTireConfiguration._REAR.radius()).divide(yTireRear));
  }

  // ---
  private static final Scalar ZERO_SPEED = Quantity.of(0, SI.VELOCITY);

  /** @param angularRate_Y_pair
   * @return {vx[m*s^-1], 0[m*s^-1], omega[s^-1]} */
  public Tensor odometryVelocity(Tensor angularRate_Y_pair) {
    return Tensors.of( //
        odometryTangentSpeed(angularRate_Y_pair), //
        ZERO_SPEED, //
        odometryTurningRate(angularRate_Y_pair));
  }
}
