// code by jph
package ch.ethz.idsc.gokart.dev.steer;

import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class SteerPidTest extends TestCase {
  public void testSimple() {
    assertNotNull(SteerPid.GLOBAL);
  }

  public void testOutside() {
    SteerPid.GLOBAL.torqueLimitClip().isOutside(Quantity.of(+2.1, "SCT"));
    SteerPid.GLOBAL.torqueLimitClip().isOutside(Quantity.of(-2.1, "SCT"));
  }
}
