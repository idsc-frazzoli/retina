// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class SteerFeedForwardTest extends TestCase {
  public void testSimple() {
    Scalar scalar = SteerFeedForward.FUNCTION.apply(Quantity.of(0.3, "SCE"));
    Chop._02.close(Quantity.of(0.27942, "SCT"), scalar);
  }

  public void testAntisym() {
    for (Tensor _a : Subdivide.increasing(Clips.absolute(SteerConfig.GLOBAL.columnMax), 100)) {
      Scalar a = _a.Get();
      assertEquals(SteerFeedForward.FUNCTION.apply(a.negate()), SteerFeedForward.FUNCTION.apply(a).negate());
    }
  }
}
