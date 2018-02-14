// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.qty.UnitConvert;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum Magnitude implements ScalarUnaryOperator {
  ONE(SI.ONE), //
  // ---
  METER(SI.METER), //
  SECOND(SI.SECOND), //
  VOLT(SI.VOLT), //
  // ---
  VELOCITY(SI.VELOCITY), //
  ACCELERATION(SI.ACCELERATION), //
  ANGULAR_RATE(SI.ANGULAR_RATE), //
  // ---
  DEGREE_ANGLE(SI.DEGREE_ANGLE), //
  DEGREE_CELSIUS(SI.DEGREE_CELSIUS), //
  ;
  private final Unit unit;

  private Magnitude(Unit unit) {
    this.unit = unit;
  }

  @Override
  public Scalar apply(Scalar scalar) {
    return UnitConvert.SI().to(unit).andThen(QuantityMagnitude.singleton(unit)).apply(scalar);
  }
}
