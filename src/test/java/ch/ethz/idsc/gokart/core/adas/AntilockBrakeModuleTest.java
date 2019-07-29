// code by jph
package ch.ethz.idsc.gokart.core.adas;

import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class AntilockBrakeModuleTest extends TestCase {
  public void testSimple() {
    SetVelSmartBrakingModule antilockBrakeModule = new SetVelSmartBrakingModule();
    antilockBrakeModule.first();
    assertFalse(antilockBrakeModule.putEvent().isPresent());
    antilockBrakeModule.last();
  }

  public void testCustom() {
    HapticSteerConfig hapticSteerConfig = new HapticSteerConfig();
    SetVelSmartBrakingModule antilockBrakeModule = new SetVelSmartBrakingModule(hapticSteerConfig);
    antilockBrakeModule.first();
    assertFalse(antilockBrakeModule.putEvent().isPresent());
    antilockBrakeModule.last();
  }

  public void testSimple1() {
    SetVelSmartBrakingModule antilockBrakeModule = new SetVelSmartBrakingModule();
    antilockBrakeModule.first();
    antilockBrakeModule.putEvent();
    antilockBrakeModule.last();
  }

  public void testSimple2() {
    SetVelSmartBrakingModule antilockBrakeModule = new SetVelSmartBrakingModule();
    antilockBrakeModule.first();
    antilockBrakeModule.rimoPutProvider.putEvent();
    antilockBrakeModule.last();
  }

  public void testSimple3() {
    SetVelSmartBrakingModule antilockBrakeModule = new SetVelSmartBrakingModule();
    antilockBrakeModule.first();
    antilockBrakeModule.smartBraking(Tensors.of( //
        Quantity.of(1, SI.PER_SECOND), //
        Quantity.of(1, SI.PER_SECOND)), //
        Tensors.of( //
            Quantity.of(6.1, SI.VELOCITY), //
            Quantity.of(0.1, SI.VELOCITY), //
            Quantity.of(1, SI.PER_SECOND)));
    antilockBrakeModule.last();
  }

  public void testSimple4() {
    SetVelSmartBrakingModule antilockBrakeModule = new SetVelSmartBrakingModule();
    antilockBrakeModule.first();
    RimoGetEvent rimoGetEvent = RimoGetEvents.create(5000, 5000);
    antilockBrakeModule.getEvent(rimoGetEvent);
    antilockBrakeModule.last();
  }
}
