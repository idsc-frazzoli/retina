// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class ClipSteerMappingTest extends TestCase {
  public void testSimple() {
    try {
      ClipSteerMapping.wrap(FittedSteerMapping.instance(), Clips.unit());
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
