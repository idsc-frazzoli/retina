// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.qty.UnitConvert;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum Magnitude implements ScalarUnaryOperator {
  SECOND(SI.SECOND), //
  VOLT(SI.VOLT), //
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
