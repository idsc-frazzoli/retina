// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.owl.car.math.CircleClearanceTracker;
import ch.ethz.idsc.owl.car.math.ClearanceTracker;
import ch.ethz.idsc.owl.car.math.EmptyClearanceTracker;
import ch.ethz.idsc.retina.util.math.SIDerived;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class SafetyConfigTest extends TestCase {
  public void testVlp16Lo() {
    Clip clip = SafetyConfig.GLOBAL.vlp16_ZClip();
    Clip.function(-1.1, -0.5).requireInside(clip.min());
    Clip.function(-0.1, +0.3).requireInside(clip.max());
  }

  public void testCircleClearanceTracker() {
    ClearanceTracker clearanceTracker = SafetyConfig.GLOBAL.getClearanceTracker(DoubleScalar.of(+1), new GokartStatusEvent(0.2f));
    assertTrue(clearanceTracker instanceof CircleClearanceTracker);
  }

  public void testEmptyClearanceTracker() {
    ClearanceTracker clearanceTracker = SafetyConfig.GLOBAL.getClearanceTracker(DoubleScalar.of(+1), new GokartStatusEvent(Float.NaN));
    assertTrue(clearanceTracker instanceof EmptyClearanceTracker);
  }

  public void testClearance() {
    Clip clip = SafetyConfig.GLOBAL.getClearanceClip();
    Clip.function(0.1, 0.3).requireInside(clip.min());
    Clip.function(3.0, 7.0).requireInside(clip.max());
  }

  public void testRateLimit() {
    assertTrue(Scalars.lessEquals(Quantity.of(1, SIDerived.RADIAN_PER_SECOND), SafetyConfig.GLOBAL.rateLimit));
  }
}
