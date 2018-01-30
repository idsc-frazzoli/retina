// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.IntegerQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum NSingle implements ScalarUnaryOperator {
  FUNCTION;
  // ---
  @Override
  public Scalar apply(Scalar scalar) {
    return IntegerQ.of(scalar) ? scalar : N.DOUBLE.apply(scalar);
  }
}
