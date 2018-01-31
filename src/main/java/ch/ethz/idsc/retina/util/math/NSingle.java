// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.IntegerQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum NSingle implements ScalarUnaryOperator {
  FUNCTION;
  // ---
  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof Quantity) {
      Quantity quantity = (Quantity) scalar;
      return Quantity.of(apply(quantity.value()), quantity.unit());
    }
    return IntegerQ.of(scalar) ? scalar : N.DOUBLE.apply(scalar);
  }
}
