// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sca.Sign;

/** maps real values to the interval [-clip.max, clip.max]
 * where values in [-clip.min clip.min] are mapped to zero.
 * 
 * implementation supports {@link Quantity}
 * 
 * application is to */
public class Clipzone implements ScalarUnaryOperator {
  private final Clip clip;
  private final Scalar zero;

  /** @param clip with 0 < min < max */
  public Clipzone(Clip clip) {
    this.clip = clip;
    zero = clip.min().zero();
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
      return zero;
    Scalar clipped = clip.apply(scalar);
    return negative ? clipped.negate() : clipped;
  }
}
