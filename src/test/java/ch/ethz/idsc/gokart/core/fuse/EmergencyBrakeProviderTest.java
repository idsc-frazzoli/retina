// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.owl.math.state.ProviderRank;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvents;
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
    EmergencyBrakeProvider ebp = EmergencyBrakeProvider.INSTANCE;
    assertTrue(ebp.isIdle());
    RimoGetEvent rimoGetEvent = RimoGetEvents.create(400, 400);
    ebp.getEvent(rimoGetEvent);
    ebp.consider(RealScalar.of(1.9));
    assertFalse(ebp.isIdle());
    Thread.sleep(450);
    assertTrue(ebp.isIdle());
  }

  public void testDontTrigger() {
    EmergencyBrakeProvider ebp = EmergencyBrakeProvider.INSTANCE;
    assertTrue(ebp.isIdle());
    RimoGetEvent rimoGetEvent = RimoGetEvents.create(400, 400);
    ebp.getEvent(rimoGetEvent);
    ebp.consider(RealScalar.of(2.5));
    assertTrue(ebp.isIdle());
  }
}
