// code by am
package ch.ethz.idsc.gokart.core.adas;

import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class PowerSteeringModuleTest extends TestCase {
  public void testSimple() {
    PowerSteeringModule powersteer = new PowerSteeringModule();
    powersteer.first();
    assertFalse(powersteer.putEvent().isPresent());
    powersteer.last();
  }

  public void testSimple1() {
    PowerSteeringModule powersteer = new PowerSteeringModule();
    powersteer.first();
    powersteer.putEvent(Quantity.of(0.2, "SCE"));
    powersteer.last();
  }
}
