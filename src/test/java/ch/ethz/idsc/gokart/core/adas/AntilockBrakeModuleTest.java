// code by jph
package ch.ethz.idsc.gokart.core.adas;

import junit.framework.TestCase;

public class AntilockBrakeModuleTest extends TestCase {
  public void testSimple() {
    AntilockBrakeModule antilockBrakeModule = new AntilockBrakeModule();
    antilockBrakeModule.first();
    assertFalse(antilockBrakeModule.putEvent().isPresent());
    antilockBrakeModule.last();
  }
}
