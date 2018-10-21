// code by jph
package ch.ethz.idsc.gokart.core.joy;

import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class TorqueVectoringHelperTest extends TestCase {
  public void testClip() {
    // Tensor clip =
    TorqueVectoringHelper.clip(RealScalar.of(1.2), RealScalar.ZERO);
    // TOOD JPH simplify function
    // System.out.println(clip);
  }
}
