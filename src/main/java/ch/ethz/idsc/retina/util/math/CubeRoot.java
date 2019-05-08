// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.sca.SignInterface;

/** gives the real-valued cube root of a given scalar.
 * the input scalar has to be an instance of the {@link SignInterface}.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/CubeRoot.html">CubeRoot</a> */
// TODO TENSOR 073 OBSOLETE
public enum CubeRoot implements ScalarUnaryOperator {
  FUNCTION;
  // ---
  private static final Scalar _1_3 = RationalScalar.of(1, 3);

  @Override
  public Scalar apply(Scalar scalar) {
    return Sign.isPositiveOrZero(scalar) //
        ? Power.of(scalar, _1_3)
        : Power.of(scalar.negate(), _1_3).negate();
  }
}
