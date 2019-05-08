// code by jph
package ch.ethz.idsc.gokart.core.adas;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class AntilockBrakeCheckConditionsTest extends TestCase {
  public void testSimple() {
    AntilockBrakeV2CheckConditions antilockBrakeModule = new AntilockBrakeV2CheckConditions();
    antilockBrakeModule.first();
    assertFalse(antilockBrakeModule.putEvent().isPresent());
    antilockBrakeModule.last();
  }

  public void testCustom() {
    AntilockBrakeV2CheckConditions antilockBrakeModule = new AntilockBrakeV2CheckConditions();
    antilockBrakeModule.first();
    antilockBrakeModule.putEvent1(
        Tensors.of(//
            Quantity.of(1, SI.PER_SECOND), //
            Quantity.of(1, SI.PER_SECOND)), //
        Tensors.of( //
            Quantity.of(0.1, SI.VELOCITY), //
            Quantity.of(1, SI.VELOCITY), //
            Quantity.of(1, SI.PER_SECOND)));
    antilockBrakeModule.last();
  }
 
    public void testSimple1() {
      AntilockBrakeV2CheckConditions antilockBrakeModule = new AntilockBrakeV2CheckConditions();
      antilockBrakeModule.first();
      antilockBrakeModule.vibrate();
      antilockBrakeModule.last();
    }
}
