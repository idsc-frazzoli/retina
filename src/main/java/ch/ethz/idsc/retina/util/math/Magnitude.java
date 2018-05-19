// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

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
  ANGULAR_RATE(SI.ANGULAR_RATE), //
  // ---
  DEGREE_CELSIUS(SI.DEGREE_CELSIUS), //
  // ---
  /** conversion to non-SI magnitude may be necessary
   * when interfacing with 3rd party code that requires input along that scale */
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
