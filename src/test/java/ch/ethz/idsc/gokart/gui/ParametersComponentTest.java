// code by jph
package ch.ethz.idsc.gokart.gui;

import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import junit.framework.TestCase;

public class ParametersComponentTest extends TestCase {
  public void testSimple() {
    new ParametersComponent(SteerConfig.GLOBAL);
  }
}
