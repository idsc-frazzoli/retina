// code by jph
package ch.ethz.idsc.gokart.core.tvec;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class PowerClip {
  private static final Scalar HALF = RealScalar.of(0.5);
  // ---
  private final Scalar halfWidth;
  private final Scalar mean;

  /** @param min
   * @param max
   * @throws Exception if min is greater than max */
  public PowerClip(Scalar min, Scalar max) {
    halfWidth = max.subtract(min).multiply(HALF);
    mean = max.add(min).multiply(HALF);
  }

  /** @param scalar with same unit as min and max, typically inside the interval [min, max]
   * @return value in the interval [-1, 1] */
  public Scalar relative(Scalar scalar) {
    return Clips.absoluteOne().apply(scalar.subtract(mean).divide(halfWidth));
  }

  /** @param scalar inside the interval [-1, 1]
   * @return scalar inside the interval [min, max] */
  public Scalar absolute(Scalar scalar) {
    return scalar.multiply(halfWidth).add(mean);
  }
}
