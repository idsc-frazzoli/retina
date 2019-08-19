// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class PiecewiseLinearFunctionTest extends TestCase {
  public void testSimple() {
    try {
      PiecewiseLinearFunction.of(Tensors.empty(), Tensors.empty());
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
