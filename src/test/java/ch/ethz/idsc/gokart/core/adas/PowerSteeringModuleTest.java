// code by am
package ch.ethz.idsc.gokart.core.adas;

import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class PowerSteeringModuleTest extends TestCase {
  public void testSimple() {
    PowerSteeringModule powerSteeringModule = new PowerSteeringModule();
    powerSteeringModule.first();
    assertFalse(powerSteeringModule.putEvent().isPresent());
    powerSteeringModule.last();
  }

  public void testSimple1() {
    PowerSteeringModule powerSteeringModule = new PowerSteeringModule();
    powerSteeringModule.first();
    powerSteeringModule.putEvent(Quantity.of(0.2, "SCE"));
    powerSteeringModule.last();
  }
}
