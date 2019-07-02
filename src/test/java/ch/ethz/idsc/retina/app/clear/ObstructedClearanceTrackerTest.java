// code by jph
package ch.ethz.idsc.retina.app.clear;

import java.io.IOException;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class ObstructedClearanceTrackerTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    assertTrue(Serialization.copy(new ObstructedClearanceTracker(RealScalar.of(1))).isObstructed(null));
  }
}
