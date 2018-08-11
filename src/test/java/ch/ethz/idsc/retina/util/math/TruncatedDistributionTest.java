// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class TruncatedDistributionTest extends TestCase {
  public void testSimple() {
    Clip clip = Clip.function(10, 11);
    Distribution distribution = TruncatedDistribution.of(NormalDistribution.of(10, 2), clip);
    Scalar scalar = RandomVariate.of(distribution);
    assertTrue(clip.isInside(scalar));
  }
}
