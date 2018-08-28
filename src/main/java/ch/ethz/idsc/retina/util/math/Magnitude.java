// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** converts a {@link Quantity} to a unit less {@link Scalar}
 * or throws an exception if the conversion is not possible */
public enum Magnitude implements ScalarUnaryOperator {
  ONE(SI.ONE), //
  // ---
  METER(SI.METER), //
  SECOND(SI.SECOND), //
  VOLT(SI.VOLT), //
  // ---
  PER_METER(SI.PER_METER), //
  // ---
  VELOCITY(SI.VELOCITY), //
  ACCELERATION(SI.ACCELERATION), //
  /** for s^-1 and rad*s^-1 */
  PER_SECOND(SI.PER_SECOND), //
  /** for s^-2 and rad*s^-2 */
  ANGULAR_ACCELERATION(SI.ANGULAR_ACCELERATION), //
  // ---
  /** conversion to non-SI magnitude may be necessary
   * when interfacing with 3rd party code that requires input along that scale */
  MILLI_SECOND(NonSI.MILLI_SECOND), //
  MICRO_SECOND(NonSI.MICRO_SECOND), //
  // ---
  DEGREE_CELSIUS(NonSI.DEGREE_CELSIUS), //
  // ---
  DEGREE_ANGLE(NonSI.DEGREE_ANGLE), //
  // ---
  ARMS(NonSI.ARMS), //
  ;
  // ---
  private final ScalarUnaryOperator scalarUnaryOperator;

  private Magnitude(Unit unit) {
    scalarUnaryOperator = QuantityMagnitude.SI().in(unit);
  }

  @Override // from ScalarUnaryOperator
  public Scalar apply(Scalar scalar) {
    return scalarUnaryOperator.apply(scalar);
  }

  /** @param scalar
   * @return double value of given scalar quantity after conversion to given unit */
  public double toDouble(Scalar scalar) {
    return apply(scalar).number().doubleValue();
  }

  /** @param scalar
   * @return short value of given scalar quantity after conversion to given unit */
  public short toShort(Scalar scalar) {
    return apply(scalar).number().shortValue();
  }

  /** @param scalar
   * @return int value of given scalar quantity after conversion to given unit */
  public int toInt(Scalar scalar) {
    return apply(scalar).number().intValue();
  }

  /** @param scalar
   * @return long value of given scalar quantity after conversion to given unit */
  public long toLong(Scalar scalar) {
    return apply(scalar).number().longValue();
  }
}
