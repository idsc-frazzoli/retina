// code by jph
package ch.ethz.idsc.gokart.core.adas;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
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
    AntilockBrakeModule antilockBrakeModule = new AntilockBrakeModule(hapticSteerConfig);
    antilockBrakeModule.first();
    assertFalse(antilockBrakeModule.putEvent().isPresent());
    antilockBrakeModule.last();
  }

  public void testSimple1() {
    AntilockBrakeModule antilockBrakeModule = new AntilockBrakeModule();
    antilockBrakeModule.first();
    antilockBrakeModule.putEvent();
    antilockBrakeModule.last();
  }

  public void testSimple2() {
    AntilockBrakeModule antilockBrakeModule = new AntilockBrakeModule();
    antilockBrakeModule.first();
    antilockBrakeModule.smartBraking(Tensors.of( //
        Quantity.of(1, SI.PER_SECOND), //
        Quantity.of(1, SI.PER_SECOND)), //
        Tensors.of( //
            Quantity.of(0.1, SI.VELOCITY), //
            Quantity.of(1, SI.VELOCITY), //
            Quantity.of(1, SI.PER_SECOND)));
    antilockBrakeModule.last();
  }
}
