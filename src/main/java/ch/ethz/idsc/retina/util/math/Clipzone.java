// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sca.Sign;

public class Clipzone implements ScalarUnaryOperator {
  private final Clip clip;

  public Clipzone(Clip clip) {
    this.clip = clip;
  }

  @Override // from ScalarUnaryOperator
  public Scalar apply(Scalar scalar) {
    boolean negative = Sign.isNegative(scalar);
    if (negative)
      scalar = scalar.negate();
    if (clip.isInside(scalar)) {
      Scalar inner = clip.rescale(scalar).multiply(clip.max());
      return negative ? inner.negate() : inner;
    }
    if (Scalars.lessThan(scalar, clip.min()))
      return RealScalar.ZERO;
    Scalar clipped = clip.apply(scalar);
    return negative ? clipped.negate() : clipped;
  }
}
