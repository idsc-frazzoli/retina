// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class GokartPoseEventsTest extends TestCase {
  public void testSimple() {
    GokartPoseEvents.getPoseEvent(Tensors.fromString("{1[m], 2[m], 3}"), RealScalar.ONE);
  }

  public void testNullFail() {
    try {
      GokartPoseEvents.getPoseEvent(null, RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
