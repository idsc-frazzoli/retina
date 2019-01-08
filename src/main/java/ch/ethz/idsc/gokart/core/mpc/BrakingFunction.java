// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.sca.Sqrt;

public enum BrakingFunction {
  ;
  // point after which the brake is effective
  private static final Scalar brakeStart = Quantity.of(2.5 / 100.0, SI.METER);
  private static final Scalar ZEROROOTOF = Quantity.of(0, "s^-4");
  // private static Scalar maxBrake = Quantity.of(2.5, SI.ACCELERATION);
  private static final Scalar linearFactor = Quantity.of(4.3996 * 100.0, SI.ACCELERATION.add(SI.METER.negate()));
  private static final Scalar quadraticFactor = Quantity.of(-1.3735 * 10000.0, SI.ACCELERATION.add(SI.METER.add(SI.METER).negate()));
  // TODO use Clip
  private static final Scalar LINMOT_START = Quantity.of(0.005, SI.METER);
  private static final Scalar LINMOT_END = Quantity.of(0.05, SI.METER);
  private static final Scalar LINMOT_RANGE = LINMOT_END.subtract(LINMOT_START);
  private static final Scalar ACCELERATION_ZERO = Quantity.of(0, SI.ACCELERATION);

  // Note: this is highly inaccurate. TODO do it more precisely
  /** get the induced braking deceleration (added to motor acceleration)
   * 
   * @param brakingPosition braking position (directly from linmot) [m]
   * @return braking deceleration */
  public static Scalar getBrakingAcceleration(Scalar brakingPosition) {
    if (Scalars.lessThan(brakingPosition, brakeStart))
      return ACCELERATION_ZERO;
    Scalar relBrake = brakingPosition.subtract(brakeStart);
    return relBrake.multiply(linearFactor).add(relBrake.multiply(relBrake).multiply(quadraticFactor));
  }

  /** get the wanted actuation position
   * 
   * @param wantedAcceleration wanted additional braking deceleration [m/s^2]
   * @return needed braking position [m] */
  // TODO return Optional
  public static Scalar getNeededBrakeActuation(Scalar wantedAcceleration) {
    if (Scalars.lessEquals(wantedAcceleration, ACCELERATION_ZERO))
      return null;
    Scalar rootOf = linearFactor.multiply(linearFactor)
        // Power.of(linearFactor, RealScalar.of(2)) //
        .add(RealScalar.of(4)//
            .multiply(quadraticFactor)//
            .multiply(wantedAcceleration));
    // this can happen in some rare cases
    Scalar D = Sqrt.of(Max.of(rootOf, ZEROROOTOF));
    Scalar top = linearFactor.negate().add(D);
    Scalar bottom = quadraticFactor.add(quadraticFactor);
    // RealScalar.of(2).multiply(quadraticFactor);
    return top.divide(bottom).add(brakeStart);
  }

  public static Scalar getRelativePosition(Scalar absolutePosition) {
    Scalar shifted = absolutePosition.subtract(LINMOT_START);
    return shifted.divide(LINMOT_RANGE);
  }
}
