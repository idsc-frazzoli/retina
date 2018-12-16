// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.gokart.dev.linmot.LinmotGetHelper;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import junit.framework.TestCase;

public class LinmotCoolingModuleTest extends TestCase {
  public void testFirstLast() throws Exception {
    LinmotCoolingModule lcm = new LinmotCoolingModule();
    lcm.first();
    lcm.last();
  }

  public void testPresent() {
    LinmotCoolingModule lcm = new LinmotCoolingModule();
    assertTrue(lcm.putEvent().isPresent());
    assertEquals(lcm.putEvent().get(), RimoPutEvent.PASSIVE);
  }

  public void testEvents() {
    LinmotCoolingModule lcm = new LinmotCoolingModule();
    assertTrue(lcm.putEvent().isPresent());
    lcm.getEvent(LinmotGetHelper.createTemperature(1000, 700));
    assertTrue(lcm.putEvent().isPresent());
    lcm.getEvent(LinmotGetHelper.createTemperature(700, 700));
    assertFalse(lcm.putEvent().isPresent());
  }
}
