// code by mcp, jph
package ch.ethz.idsc.demo.mp.pid;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class PIDTuningParamsTest extends TestCase {
  public void testClip() {
    Scalar angle = PIDTuningParams.GLOBAL.clip.apply(Quantity.of(.2, ""));
    System.out.println(angle);
  }
}
