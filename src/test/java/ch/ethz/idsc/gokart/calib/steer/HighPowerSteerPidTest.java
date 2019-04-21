// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class HighPowerSteerPidTest extends TestCase {
  public void testSimple() {
    assertNotNull(HighPowerSteerPid.GLOBAL);
  }

  public void testOutside() {
    HighPowerSteerPid.GLOBAL.torqueLimitClip().isOutside(Quantity.of(+3.1, "SCT"));
    HighPowerSteerPid.GLOBAL.torqueLimitClip().isOutside(Quantity.of(-3.1, "SCT"));
  }
}
