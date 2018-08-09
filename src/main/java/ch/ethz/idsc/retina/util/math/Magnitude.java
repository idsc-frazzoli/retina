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
  ANGULAR_RATE(SI.ANGULAR_RATE), // for s^-1 and rad*s^-1
  // ---
  /** conversion to non-SI magnitude may be necessary
   * when interfacing with 3rd party code that requires input along that scale */
  MILLI_SECOND(NonSI.MILLI_SECOND), //
  MICRO_SECOND(NonSI.MICRO_SECOND), //
  // ---
  DEGREE_CELSIUS(NonSI.DEGREE_CELSIUS), //
  // ---
  DEGREE_ANGLE(NonSI.DEGREE_ANGLE), //
  ;
  // ---
  private final ScalarUnaryOperator scalarUnaryOperator;

  private Magnitude(Unit unit) {
    scalarUnaryOperator = QuantityMagnitude.SI().in(unit);
  }

  @Override
  public Scalar apply(Scalar scalar) {
    return scalarUnaryOperator.apply(scalar);
  }
}
