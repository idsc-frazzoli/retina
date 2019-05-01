// code by am
package ch.ethz.idsc.gokart.core.adas;

import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class PowerSteeringModuleTest extends TestCase {
  public void testSimple() {
    PowerSteeringV1Module powerSteeringModule = new PowerSteeringV1Module();
    powerSteeringModule.first();
    assertFalse(powerSteeringModule.putEvent().isPresent());
    powerSteeringModule.last();
  }

  public void testSimple1() {
    PowerSteeringV1Module powerSteeringModule = new PowerSteeringV1Module();
    powerSteeringModule.first();
    powerSteeringModule.putEvent(Quantity.of(0.2, "SCE"));
    powerSteeringModule.last();
  }
}
