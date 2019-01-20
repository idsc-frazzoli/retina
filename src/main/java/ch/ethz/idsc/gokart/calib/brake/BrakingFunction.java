// code by mh
package ch.ethz.idsc.gokart.calib.brake;

import java.util.Optional;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.sca.Ramp;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.sca.Sqrt;

// TODO JPH use java conventions
public enum BrakingFunction {
  ;
  // point after which the brake is effective
  private static final Scalar BRAKE_START = Quantity.of(2.5 / 100.0, SI.METER);
  private static final Scalar ZEROROOTOF = Quantity.of(0, "s^-4");
  // private static Scalar maxBrake = Quantity.of(2.5, SI.ACCELERATION);
  private static final Scalar LINEAR_FACTOR = Quantity.of(4.3996 * 100.0, SI.ACCELERATION.add(SI.METER.negate()));
  private static final Scalar QUADRATIC_FACTOR = Quantity.of(-1.3735 * 10000.0, SI.ACCELERATION.add(SI.METER.add(SI.METER).negate()));
  private static final Tensor COEFFS = Tensors.of(RealScalar.ZERO, LINEAR_FACTOR, QUADRATIC_FACTOR);
  private static final ScalarUnaryOperator BRAKING_ACCELERATION = Series.of(COEFFS);
  // TODO JPH use Clip
  private static final Scalar LINMOT_START = Quantity.of(0.005, SI.METER);
  private static final Scalar LINMOT_END = Quantity.of(0.05, SI.METER);
  private static final Scalar LINMOT_RANGE = LINMOT_END.subtract(LINMOT_START);

  // Note: this is highly inaccurate. TODO do it more precisely
  /** get the induced braking deceleration (added to motor acceleration)
   * 
   * @param brakingPosition braking position (directly from linmot) [m]
   * @return braking deceleration */
  public static Scalar getBrakingAcceleration(Scalar brakingPosition) {
    // FIXME JPH/MH cap result at some max value
    return BRAKING_ACCELERATION.apply(Ramp.FUNCTION.apply(brakingPosition.subtract(BRAKE_START)));
  }

  /** get the wanted actuation position
   * 
   * @param wantedAcceleration wanted additional braking deceleration [m/s^2]
   * @return needed braking position [m] */
  public static Optional<Scalar> getNeededBrakeActuation(Scalar wantedAcceleration) {
    if (Sign.isNegativeOrZero(wantedAcceleration))
      return Optional.empty();
    // TODO MH use Roots.of(COEFFS);
    Scalar rootOf = LINEAR_FACTOR.multiply(LINEAR_FACTOR)
        // Power.of(linearFactor, RealScalar.of(2)) //
        .add(RealScalar.of(4) //
            .multiply(QUADRATIC_FACTOR) //
            .multiply(wantedAcceleration));
    // this can happen in some rare cases
    Scalar D = Sqrt.of(Max.of(rootOf, ZEROROOTOF));
    Scalar top = LINEAR_FACTOR.negate().add(D);
    Scalar bottom = QUADRATIC_FACTOR.add(QUADRATIC_FACTOR);
    // RealScalar.of(2).multiply(quadraticFactor);
    return Optional.of(top.divide(bottom).add(BRAKE_START));
  }

  public static Scalar getRelativePosition(Scalar absolutePosition) {
    Scalar shifted = absolutePosition.subtract(LINMOT_START);
    return shifted.divide(LINMOT_RANGE);
  }
}
