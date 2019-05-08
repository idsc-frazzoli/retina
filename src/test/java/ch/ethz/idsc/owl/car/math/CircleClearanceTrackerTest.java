// code by jph
package ch.ethz.idsc.owl.car.math;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clips;
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

  public void testQuantity() {
    CircleClearanceTracker circleClearanceTracker = //
        new CircleClearanceTracker(Quantity.of(2, SI.VELOCITY), ChassisGeometry.GLOBAL.yHalfWidth, //
            Quantity.of(0.2, SI.PER_METER), PoseHelper.attachUnits(Tensors.vector(0.1, 0.01, 0.01)), //
            Clips.interval(Quantity.of(0.1, SI.SECOND), Quantity.of(+1.0, SI.SECOND)));
    assertFalse(circleClearanceTracker.contact().isPresent());
    boolean obstructed = circleClearanceTracker.isObstructed(PoseHelper.attachUnits(Tensors.vector(0.6, 0.1, 0.05)));
    assertTrue(obstructed);
    Quantity time = (Quantity) circleClearanceTracker.contact().get();
    Clips.interval(0.3, 0.4).requireInside(time.value());
    assertEquals(time.unit(), SI.SECOND);
    PoseHelper.toUnitless(circleClearanceTracker.violation().get());
  }
}
