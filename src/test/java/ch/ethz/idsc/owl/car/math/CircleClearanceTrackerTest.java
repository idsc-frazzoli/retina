// code by jph
package ch.ethz.idsc.owl.car.math;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class CircleClearanceTrackerTest extends TestCase {
  public void testStraight() {
    GokartStatusEvent gokartStatusEvent = new GokartStatusEvent(0);
    ClearanceTracker clearanceTracker = SafetyConfig.GLOBAL.getClearanceTracker(gokartStatusEvent);
    assertFalse(clearanceTracker.contact().isPresent());
    assertTrue(clearanceTracker.isObstructed(Tensors.vector(3, 0)));
    assertTrue(clearanceTracker.contact().isPresent());
    assertFalse(clearanceTracker.isObstructed(Tensors.vector(3, -1)));
    assertFalse(clearanceTracker.isObstructed(Tensors.vector(3, +1)));
    assertFalse(clearanceTracker.isObstructed(Tensors.vector(-3, 0)));
    assertFalse(clearanceTracker.isObstructed(Tensors.vector(-3, -1)));
    assertFalse(clearanceTracker.isObstructed(Tensors.vector(-3, +1)));
  }

  public void testCurved() {
    GokartStatusEvent gokartStatusEvent = new GokartStatusEvent(0.5f);
    ClearanceTracker clearanceTracker = SafetyConfig.GLOBAL.getClearanceTracker(gokartStatusEvent);
    assertFalse(clearanceTracker.isObstructed(Tensors.vector(3, 0)));
    assertFalse(clearanceTracker.isObstructed(Tensors.vector(3, -1)));
    assertFalse(clearanceTracker.contact().isPresent());
    assertTrue(clearanceTracker.isObstructed(Tensors.vector(3, +1)));
    assertTrue(clearanceTracker.contact().isPresent());
    assertFalse(clearanceTracker.isObstructed(Tensors.vector(-3, 0)));
    assertFalse(clearanceTracker.isObstructed(Tensors.vector(-3, -1)));
    assertFalse(clearanceTracker.isObstructed(Tensors.vector(-3, +1)));
  }
}
