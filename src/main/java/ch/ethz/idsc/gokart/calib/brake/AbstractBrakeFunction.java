// code by mh, jph
package ch.ethz.idsc.gokart.calib.brake;

import ch.ethz.idsc.gokart.dev.linmot.LinmotPutHelper;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Roots;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Ramp;
import ch.ethz.idsc.tensor.sca.Real;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sca.Sign;

public abstract class AbstractBrakeFunction {
  /** old value: 4.3996 * 100.0 == 439.96 */
  /** new data (measured with IMU): 2.5555 * 100.0 == 255.55 */
  private static final Scalar LINEAR_FACTOR = Quantity.of(255.55, SI.ACCELERATION.add(SI.METER.negate()));
  /** old value: -1.3735 * 10000.0 == -13735.0 */
  /** new data (measured with IMU) -0.0008 * 10000.0 == -8.0 */
  private static final Scalar QUADRATIC_FACTOR = Quantity.of(-8.0, SI.ACCELERATION.add(SI.METER.add(SI.METER).negate()));
  private static final Tensor COEFFS = Tensors.of(RealScalar.ZERO, LINEAR_FACTOR, QUADRATIC_FACTOR).unmodifiable();
  private static final ScalarUnaryOperator BRAKING_ACCELERATION = Series.of(COEFFS);
  /** point after which the brake is effective
   * 2.5 / 100.0 == 0.025 */
  private static final Scalar BRAKE_START = Quantity.of(0.025, SI.METER);

  // Note: this is highly inaccurate. TODO do it more precisely
  /** get the induced braking deceleration (added to motor acceleration)
   * 
   * @param brakingPosition [m]
   * @return braking deceleration in [m*s^-2] */
  static Scalar decelerationFromPos(Scalar brakingPosition) {
    return Ramp.FUNCTION.apply(BRAKING_ACCELERATION.apply(Ramp.FUNCTION.apply(brakingPosition.subtract(BRAKE_START))));
  }

  /** @param brakingPosition [m]
   * @param factor
   * @return braking deceleration in [m*s^-2] */
  static Scalar getDeceleration(Scalar brakingPosition, Scalar factor) {
    // TODO JPH test
    return decelerationFromPos(brakingPosition).multiply(factor);
  }

  /** get the wanted actuation position
   * 
   * @param wantedDeceleration wanted additional braking deceleration [m*s^-2] positive for braking effect
   * @param factor the scaling factor for the curve (comes from brake heat/body weight)
   * @return needed braking position [m] positive for braking effect */
  static Scalar getNeededBrakeActuation(Scalar wantedDeceleration, Scalar factor) {
    if (Sign.isNegativeOrZero(wantedDeceleration))
      return LinmotPutHelper.scalePositive().min();
    Tensor coeffs = COEFFS.copy().multiply(factor);
    coeffs.set(scalar -> scalar.subtract(wantedDeceleration), 0); // poly(x) == y -> poly(x) - y == 0
    Tensor roots = Roots.of(coeffs);
    return Real.FUNCTION.apply(roots.Get(0)).add(BRAKE_START);
  }

  /** @param absolutePosition brake position [m] 0.005 in rest position and growing for pushed brake
   * @return value in the interval [0, 1] */
  static Scalar getRelativePosition(Scalar absolutePosition) {
    return LinmotPutHelper.scalePositive().rescale(absolutePosition);
  }

  /** @param wantedDeceleration positive for braking effect with unit [m*s^-2]
   * @return value in the interval [0, 1] */
  public final Scalar getRelativeBrakeActuation(Scalar wantedDeceleration) {
    return getRelativePosition(getNeededBrakeActuation(wantedDeceleration));
  }

  /** get the induced braking deceleration (added to motor acceleration)
   * 
   * @param brakingPosition [m]
   * @return braking deceleration */
  abstract Scalar getDeceleration(Scalar brakingPosition);

  /** get the wanted actuation position
   * 
   * @param wantedDeceleration wanted additional braking deceleration [m*s^-2] positive for braking effect
   * @return needed braking position [m] positive for braking effect */
  abstract Scalar getNeededBrakeActuation(Scalar wantedDeceleration);
}
