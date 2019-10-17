// code by jph
package ch.ethz.idsc.retina.app.clear;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class CircleClearanceTrackerTest extends TestCase {
  public void testStraight() {
    ClearanceTracker clearanceTracker = SafetyConfig.GLOBAL.getClearanceTracker(DoubleScalar.of(+1), RealScalar.of(0.0));
    assertFalse(clearanceTracker.contact().isPresent());
    assertTrue(clearanceTracker.isObstructed(Tensors.vector(3, 0)));
    assertTrue(clearanceTracker.contact().isPresent());
    assertFalse(clearanceTracker.isObstructed(Tensors.vector(3, -1)));
    assertFalse(clearanceTracker.isObstructed(Tensors.vector(3, +1)));
    assertFalse(clearanceTracker.isObstructed(Tensors.vector(-3, 0)));
    assertFalse(clearanceTracker.isObstructed(Tensors.vector(-3, -1)));
    assertFalse(clearanceTracker.isObstructed(Tensors.vector(-3, +1)));
    assertTrue(clearanceTracker.contact().isPresent());
  }

  public void testCurved() {
    ClearanceTracker clearanceTracker = SafetyConfig.GLOBAL.getClearanceTracker(DoubleScalar.of(+1), RealScalar.of(0.5));
    assertFalse(clearanceTracker.isObstructed(Tensors.vector(3, 0)));
    assertFalse(clearanceTracker.isObstructed(Tensors.vector(3, -1)));
    assertFalse(clearanceTracker.contact().isPresent());
    assertTrue(clearanceTracker.isObstructed(Tensors.vector(2, +1.5)));
    assertTrue(clearanceTracker.contact().isPresent());
    assertFalse(clearanceTracker.isObstructed(Tensors.vector(-3, 0)));
    assertFalse(clearanceTracker.isObstructed(Tensors.vector(-3, -1)));
    assertFalse(clearanceTracker.isObstructed(Tensors.vector(-3, +1)));
    assertTrue(clearanceTracker.contact().isPresent());
  }

  public void testStraightNeg() {
    ClearanceTracker clearanceTracker = SafetyConfig.GLOBAL.getClearanceTracker(DoubleScalar.of(-1), RealScalar.of(0.0));
    assertFalse(clearanceTracker.contact().isPresent());
    assertFalse(clearanceTracker.isObstructed(Tensors.vector(3, 0)));
    assertFalse(clearanceTracker.isObstructed(Tensors.vector(3, -1)));
    assertFalse(clearanceTracker.isObstructed(Tensors.vector(3, +1)));
    assertFalse(clearanceTracker.contact().isPresent());
    assertTrue(clearanceTracker.isObstructed(Tensors.vector(-3, 0)));
    assertTrue(clearanceTracker.contact().isPresent());
    assertFalse(clearanceTracker.isObstructed(Tensors.vector(-3, -1)));
    assertFalse(clearanceTracker.isObstructed(Tensors.vector(-3, +1)));
  }

  public void testCurvedNeg() {
    ClearanceTracker clearanceTracker = SafetyConfig.GLOBAL.getClearanceTracker(DoubleScalar.of(-1), RealScalar.of(0.5));
    assertFalse(clearanceTracker.isObstructed(Tensors.vector(3, 0)));
    assertFalse(clearanceTracker.isObstructed(Tensors.vector(3, -1)));
    assertFalse(clearanceTracker.isObstructed(Tensors.vector(3, +1)));
    assertFalse(clearanceTracker.contact().isPresent());
    // assertFalse(clearanceTracker.isObstructed(Tensors.vector(-3, 0)));
    // assertFalse(clearanceTracker.isObstructed(Tensors.vector(-3, -1)));
    // assertFalse(clearanceTracker.isObstructed(Tensors.vector(-3, +1)));
  }
}
