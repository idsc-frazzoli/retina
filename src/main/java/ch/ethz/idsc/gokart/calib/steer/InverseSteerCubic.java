// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Roots;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** Hint:
 * INVERSE IS ONLY VALID FOR CERTAIN CUBIC POLYNOMIALS
 * AND ONLY FOR PARAMETERS OF A CERTAIN RANGE */
/* package */ class InverseSteerCubic implements ScalarUnaryOperator {
  private final Scalar b;
  private final Scalar d;

  /** @param b linear coefficient
   * @param d cubic coefficient */
  public InverseSteerCubic(Scalar b, Scalar d) {
    this.b = b;
    this.d = d;
  }

  @Override
  public Scalar apply(Scalar y) {
    return Chop._10.apply(Roots.of(Tensors.of(y.negate(), b, RealScalar.ZERO, d)).Get(1));
  }
}
