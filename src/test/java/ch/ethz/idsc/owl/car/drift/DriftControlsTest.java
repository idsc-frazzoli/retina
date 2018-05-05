// code by jph
package ch.ethz.idsc.owl.car.drift;

import java.util.Collection;

import ch.ethz.idsc.owl.math.flow.Flow;
import junit.framework.TestCase;

public class DriftControlsTest extends TestCase {
  public void testSimple() {
    Collection<Flow> controls = new DriftStandardFlows().getFlows(10);
    assertEquals(controls.size(), 11);
  }

  public void testMakeOdd() {
    Collection<Flow> controls = new DriftStandardFlows().getFlows(11);
    assertEquals(controls.size(), 13);
  }
}
