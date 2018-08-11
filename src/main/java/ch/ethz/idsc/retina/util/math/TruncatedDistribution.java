// code by jph
package ch.ethz.idsc.retina.util.math;

import java.io.Serializable;
import java.util.Random;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.RandomVariateInterface;
import ch.ethz.idsc.tensor.sca.Clip;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/TruncatedDistribution.html">TruncatedDistribution</a> */
public class TruncatedDistribution implements Distribution, RandomVariateInterface, Serializable {
  public static Distribution of(Distribution distribution, Clip clip) {
    return new TruncatedDistribution(distribution, clip);
  }

  // ---
  private final Distribution distribution;
  private final Clip clip;

  private TruncatedDistribution(Distribution distribution, Clip clip) {
    this.distribution = distribution;
    this.clip = clip;
  }

  @Override
  public Scalar randomVariate(Random random) {
    while (true) {
      Scalar scalar = RandomVariate.of(distribution);
      if (clip.isInside(scalar))
        return scalar;
    }
  }
}
