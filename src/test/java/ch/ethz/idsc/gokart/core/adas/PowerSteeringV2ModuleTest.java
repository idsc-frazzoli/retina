// code by am
package ch.ethz.idsc.gokart.core.adas;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class PowerSteeringV2ModuleTest extends TestCase {
  public void testSimple() {
    PowerSteeringV2Module powerSteeringModule = new PowerSteeringV2Module();
    powerSteeringModule.first();
    assertFalse(powerSteeringModule.putEvent().isPresent());
    powerSteeringModule.last();
  }

  public void testSimple1() {
    PowerSteeringV2Module powerSteeringModule = new PowerSteeringV2Module();
    powerSteeringModule.first();
    powerSteeringModule.putEvent(Quantity.of(0.2, "SCE"),
        Tensors.of( //
            Quantity.of(0.1, SI.VELOCITY), //
            Quantity.of(1, SI.VELOCITY), //
            Quantity.of(1, SI.PER_SECOND)),//
        0);
    powerSteeringModule.last();
  }
}
