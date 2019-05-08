// code by jph
package ch.ethz.idsc.gokart.gui;

import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import junit.framework.TestCase;

public class ParametersComponentTest extends TestCase {
  public void testSimple() throws Exception {
    new ParametersComponent(SteerConfig.GLOBAL);
  }

  public void testFailNull() {
    try {
      new ParametersComponent(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
