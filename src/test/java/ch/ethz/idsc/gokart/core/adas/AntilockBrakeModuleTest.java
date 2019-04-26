// code by jph
package ch.ethz.idsc.gokart.core.adas;

import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class AntilockBrakeModuleTest extends TestCase {
  public void testSimple() {
    AntilockBrakeModule antilockBrakeModule = new AntilockBrakeModule();
    antilockBrakeModule.first();
    assertFalse(antilockBrakeModule.putEvent().isPresent());
    antilockBrakeModule.last();
  }

  public void testCustom() {
    HapticSteerConfig hapticSteerConfig = new HapticSteerConfig();
    hapticSteerConfig.absAmplitude = RealScalar.of(3);
    AntilockBrakeModule antilockBrakeModule = new AntilockBrakeModule(hapticSteerConfig);
    antilockBrakeModule.first();
    assertFalse(antilockBrakeModule.putEvent().isPresent());
    antilockBrakeModule.last();
  }
}
