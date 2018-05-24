// code by jph
package ch.ethz.idsc.demo.mg.util;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** https://en.wikipedia.org/wiki/Horner%27s_method */
// TODO JAN class is obsolete with tensor v055
/* package */ class HornerScheme implements ScalarUnaryOperator {
  private final Tensor reversed;

  /* package */ HornerScheme(Tensor coeffs) {
    reversed = Reverse.of(coeffs);
  }

  @Override
  public Scalar apply(Scalar scalar) {
    Scalar total = scalar.zero();
    for (Tensor entry : reversed)
      total = total.multiply(scalar).add(entry);
    return total;
  }
}
