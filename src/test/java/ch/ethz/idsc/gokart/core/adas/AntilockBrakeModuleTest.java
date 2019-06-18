// code by jph
package ch.ethz.idsc.gokart.core.adas;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class AntilockBrakeModuleTest extends TestCase {
  public void testSimple() {
    SetVelNotsmartBrakingModule antilockBrakeModule = new SetVelNotsmartBrakingModule();
    antilockBrakeModule.first();
    assertFalse(antilockBrakeModule.putEvent().isPresent());
    antilockBrakeModule.last();
  }

  public void testCustom() {
    HapticSteerConfig hapticSteerConfig = new HapticSteerConfig();
    SetVelNotsmartBrakingModule antilockBrakeModule = new SetVelNotsmartBrakingModule(hapticSteerConfig);
    antilockBrakeModule.first();
    assertFalse(antilockBrakeModule.putEvent().isPresent());
    antilockBrakeModule.last();
  }

  public void testSimple1() {
    SetVelNotsmartBrakingModule antilockBrakeModule = new SetVelNotsmartBrakingModule();
    antilockBrakeModule.first();
    antilockBrakeModule.putEvent();
    antilockBrakeModule.last();
  }

  public void testSimple2() {
    SetVelNotsmartBrakingModule antilockBrakeModule = new SetVelNotsmartBrakingModule();
    antilockBrakeModule.first();
    antilockBrakeModule.notsmartBraking(Tensors.of( //
        Quantity.of(1, SI.PER_SECOND), //
        Quantity.of(1, SI.PER_SECOND)), //
        Tensors.of( //
            Quantity.of(6.1, SI.VELOCITY), //
            Quantity.of(0.1, SI.VELOCITY), //
            Quantity.of(1, SI.PER_SECOND)));
    antilockBrakeModule.last();
  }
}
