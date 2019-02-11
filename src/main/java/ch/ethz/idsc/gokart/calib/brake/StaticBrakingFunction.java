// code by mh, jph
package ch.ethz.idsc.gokart.calib.brake;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Roots;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Ramp;
import ch.ethz.idsc.tensor.sca.Real;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sca.Sign;

public class StaticBrakingFunction {
  /** point after which the brake is effective
   * 2.5 / 100.0 == 0.025 */
  private static final Scalar BRAKE_START = Quantity.of(0.025, SI.METER);
  /** 4.3996 * 100.0 == 439.96 */
  private static final Scalar LINEAR_FACTOR = Quantity.of(439.96, SI.ACCELERATION.add(SI.METER.negate()));
  /** -1.3735 * 10000.0 == -13735.0 */
  private static final Scalar QUADRATIC_FACTOR = Quantity.of(-13735.0, SI.ACCELERATION.add(SI.METER.add(SI.METER).negate()));
  protected static final Tensor COEFFS = Tensors.of(RealScalar.ZERO, LINEAR_FACTOR, QUADRATIC_FACTOR).unmodifiable();
  private static final ScalarUnaryOperator BRAKING_ACCELERATION = Series.of(COEFFS);
  // TODO JH this is redundant to Linmot constants
  private static final Clip LINMOT_CLIP = Clip.function( //
      Quantity.of(0.005, SI.METER), //
      Quantity.of(0.050, SI.METER));

  protected StaticBrakingFunction() {
  }

  // Note: this is highly inaccurate. TODO do it more precisely
  /** get the induced braking deceleration (added to motor acceleration)
   * 
   * @param brakingPosition [m]
   * @return braking deceleration */
  // @Deprecated // TODO: JPH, why is this Deprecated?
  protected Scalar getAcceleration(Scalar brakingPosition, Scalar brakeCurveFactor) {
    // FIXME JPH/MH cap result at some max value
    return Ramp.FUNCTION.apply(BRAKING_ACCELERATION.apply(Ramp.FUNCTION.apply(brakingPosition.subtract(BRAKE_START)))).multiply(brakeCurveFactor);
  }

  /** get the induced braking deceleration (added to motor acceleration)
   * 
   * @param brakingPosition [m]
   * @return braking deceleration */
  // @Deprecated // TODO: JPH, why is this Deprecated?
  Scalar getDeceleration(Scalar brakingPosition) {
    return getAcceleration(brakingPosition, RealScalar.ONE);
  }

  /** @param wantedAcceleration positive for braking effect with unit [m*s^-2]
   * @return value in the interval [0, 1] */
  public Scalar getRelativeBrakeActuation(Scalar wantedAcceleration) {
    return getRelativePosition(getNeededBrakeActuation(wantedAcceleration));
  }

  /** get the wanted actuation position
   * 
   * @param wantedDeceleration wanted additional braking deceleration [m*s^-2] positive for braking effect
   * @param brakeCurveMultiplicator the scaling factor for the curve (comes from brake heat/body weight)
   * @return needed braking position [m] positive for braking effect */
  public Scalar getNeededBrakeActuation(Scalar wantedDeceleration, Scalar brakeCurveMultiplicator) {
    if (Sign.isNegativeOrZero(wantedDeceleration))
      return LINMOT_CLIP.min();
    Tensor coeffs = COEFFS.copy().multiply(brakeCurveMultiplicator);
    coeffs.set(scalar -> scalar.subtract(wantedDeceleration), 0); // poly(x) == y -> poly(x) - y == 0
    Tensor roots = Roots.of(coeffs);
    return Real.FUNCTION.apply(roots.Get(0)).add(BRAKE_START);
  }

  /** get the wanted actuation position
   * 
   * @param wantedDeceleration wanted additional braking deceleration [m*s^-2] positive for braking effect
   * @return needed braking position [m] positive for braking effect */
  Scalar getNeededBrakeActuation(Scalar wantedDeceleration) {
    return getNeededBrakeActuation(wantedDeceleration, RealScalar.ONE);
  }

  /** @param absolutePosition brake position [m] 0.005 in rest position and growing for pushed brake
   * @return value in the interval [0, 1] */
  static Scalar getRelativePosition(Scalar absolutePosition) {
    return LINMOT_CLIP.rescale(absolutePosition);
  }
}
