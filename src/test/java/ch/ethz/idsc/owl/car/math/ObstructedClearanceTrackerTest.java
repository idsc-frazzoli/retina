// code by jph
package ch.ethz.idsc.owl.car.math;

import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class ObstructedClearanceTrackerTest extends TestCase {
  public void testSimple() {
    assertTrue(new ObstructedClearanceTracker(RealScalar.of(1)).isObstructed(null));
  }
}
