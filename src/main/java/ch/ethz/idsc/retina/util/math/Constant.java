// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;

public enum Constant {
  GOLDEN_ANGLE(DoubleScalar.of(2.3999632297286533222)), // (3-sqrt(5))/pi
  ;
  public final Scalar value;

  private Constant(Scalar value) {
    this.value = value;
  }
}
