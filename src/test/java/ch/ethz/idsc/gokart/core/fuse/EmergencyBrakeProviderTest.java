// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class EmergencyBrakeProviderTest extends TestCase {
  public void testMargin() {
    assertEquals(EmergencyBrakeProvider.INSTANCE.marginMeter(), DoubleScalar.of(1.66));
  }

  public void testRank() {
    assertEquals(EmergencyBrakeProvider.INSTANCE.getProviderRank(), ProviderRank.EMERGENCY);
  }

  public void testTrigger() throws InterruptedException {
    EmergencyBrakeProvider emergencyBrakeProvider = EmergencyBrakeProvider.INSTANCE;
    assertTrue(emergencyBrakeProvider.isIdle());
    RimoGetEvent rimoGetEvent = RimoGetEvents.create(400, 400);
    emergencyBrakeProvider.getEvent(rimoGetEvent);
    emergencyBrakeProvider.consider(RealScalar.of(1.9));
    assertFalse(emergencyBrakeProvider.isIdle());
    Thread.sleep(450);
    assertTrue(emergencyBrakeProvider.isIdle());
  }

  public void testDontTrigger() {
    EmergencyBrakeProvider emergencyBrakeProvider = EmergencyBrakeProvider.INSTANCE;
    assertTrue(emergencyBrakeProvider.isIdle());
    RimoGetEvent rimoGetEvent = RimoGetEvents.create(400, 400);
    emergencyBrakeProvider.getEvent(rimoGetEvent);
    emergencyBrakeProvider.consider(RealScalar.of(2.5));
    assertTrue(emergencyBrakeProvider.isIdle());
  }
}
