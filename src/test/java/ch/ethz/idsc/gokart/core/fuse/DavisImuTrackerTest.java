// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class DavisImuTrackerTest extends TestCase {
  public void testSimple() {
    DavisImuTracker.INSTANCE.setGyroZ(Quantity.of(1.3, SI.PER_SECOND));
    int framecount = DavisImuTracker.INSTANCE.getFramecount();
    assertTrue(0 <= framecount);
    Scalar gyroZ = DavisImuTracker.INSTANCE.getGyroZ();
    assertFalse(ExactScalarQ.of(gyroZ));
  }

  public void testSetGyroZ() {
    DavisImuTracker.INSTANCE.setGyroZ(Quantity.of(1, SI.PER_SECOND));
    Scalar gyroZ = DavisImuTracker.INSTANCE.getGyroZ();
    assertEquals(gyroZ, Quantity.of(1, SI.PER_SECOND));
    DavisImuTracker.INSTANCE.setGyroZ(Quantity.of(0.0, SI.PER_SECOND));
    assertEquals(DavisImuTracker.INSTANCE.getGyroZ(), Quantity.of(0, SI.PER_SECOND));
  }
}
