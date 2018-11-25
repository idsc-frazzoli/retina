package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.Sqrt;

public enum BrakingFunction {
  ;
  // point after which the brake is effective
  private static Scalar brakeStart = Quantity.of(2.5 / 100.0, SI.METER);
  // private static Scalar maxBrake = Quantity.of(2.5, SI.ACCELERATION);
  private static Scalar linearFactor = Quantity.of(4.3996 * 100.0, SI.ACCELERATION.add(SI.METER.negate()));
  private static Scalar quadraticFactor = Quantity.of(-1.3735 * 10000.0, SI.ACCELERATION.add(SI.METER.add(SI.METER).negate()));
  private static Scalar LINMOTSTART = Quantity.of(0.005, SI.METER);
  private static Scalar LINMOTEND = Quantity.of(0.05, SI.METER);
  private static Scalar LINMOTRANGE = LINMOTEND.subtract(LINMOTSTART);

  // Note: this is highly innacurate. TODO: do it more precisely
  /** get the induced braking deceleration (added to motor acceleration)
   * 
   * @param brakingPosition braking position (directly from linmot) [m]
   * @return braking deceleration */
  public static Scalar getBrakingAcceleration(Scalar brakingPosition) {
    if (Scalars.lessThan(brakingPosition, brakeStart))
      return Quantity.of(0, SI.ACCELERATION);
    Scalar relBrake = brakingPosition.subtract(brakeStart);
    return relBrake.multiply(linearFactor).add(relBrake.multiply(relBrake).multiply(quadraticFactor));
  }

  /** get the wanted actuation position
   * 
   * @param wantedAcceleration wanted additional braking deceleration [m/s^2]
   * @return needed braking position [m] */
  public static Scalar getNeededBrakeActuation(Scalar wantedAcceleration) {
    if (Scalars.lessEquals(wantedAcceleration, Quantity.of(0, SI.ACCELERATION))) {
      return null;
    }
    Scalar D = Sqrt.of(//
        Power.of(linearFactor, RealScalar.of(2))//
            .add(RealScalar.of(4)//
                .multiply(quadraticFactor)//
                .multiply(wantedAcceleration)));
    Scalar top = linearFactor.negate().add(D);
    Scalar bottom = RealScalar.of(2).multiply(quadraticFactor);
    return top.divide(bottom).add(brakeStart);
  }

  public static Scalar getRelativePosition(Scalar absolutePosition) {
    Scalar shifted = absolutePosition.subtract(LINMOTSTART);
    Scalar shiftedAndNormalized = shifted.divide(LINMOTRANGE);
    return shiftedAndNormalized;
  }
}
