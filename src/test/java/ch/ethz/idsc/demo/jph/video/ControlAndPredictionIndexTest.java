// code by jph
package ch.ethz.idsc.demo.jph.video;

import ch.ethz.idsc.tensor.io.HomeDirectory;
import junit.framework.TestCase;

public class ControlAndPredictionIndexTest extends TestCase {
  public void testSimple() {
    try {
      ControlAndPredictionStepsIndex.build(HomeDirectory.file("does_not_exist.lcm"));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
