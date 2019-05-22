// code by mcp, jph
package ch.ethz.idsc.demo.mp.pid;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class PIDTuningParamsTest extends TestCase {
  public void testClipTrue() {
    Scalar angle = PIDTuningParams.GLOBAL.clipRatio().apply(Quantity.of(10, SI.PER_METER));
    assertTrue(Chop._03.close(angle, PIDTuningParams.GLOBAL.maxSteerTurningRatio));
  }

  public void testClipFalse() {
    Scalar angle = PIDTuningParams.GLOBAL.clipRatio().apply(Quantity.of(0.4, SI.PER_METER));
    assertNotSame(angle, PIDTuningParams.GLOBAL.maxSteerTurningRatio);
  }
}
