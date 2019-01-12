// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetHelper;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import junit.framework.TestCase;

public class LinmotSafetyModuleTest extends TestCase {
  public void testFirstLast() throws Exception {
    LinmotSafetyModule linmotSafetyModule = new LinmotSafetyModule();
    for (int count = 0; count < 10; ++count) {
      linmotSafetyModule.first();
      linmotSafetyModule.last();
    }
    assertEquals(linmotSafetyModule.getProviderRank(), ProviderRank.SAFETY);
  }

  public void testOperational() throws Exception {
    LinmotSafetyModule linmotSafetyModule = new LinmotSafetyModule();
    linmotSafetyModule.first();
    assertTrue(linmotSafetyModule.putEvent().isPresent());
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
    LinmotGetEvent linmotGetEvent = new LinmotGetEvent(byteBuffer);
    linmotSafetyModule.getEvent(linmotGetEvent);
    assertTrue(linmotSafetyModule.putEvent().isPresent()); // not operational
    assertEquals(linmotSafetyModule.putEvent().get(), RimoPutEvent.PASSIVE);
    linmotSafetyModule.getEvent(LinmotGetHelper.createTemperature(300, 300));
    assertFalse(linmotSafetyModule.putEvent().isPresent()); // timeout
    linmotSafetyModule.getEvent(LinmotGetHelper.createNonOperational());
    assertEquals(linmotSafetyModule.putEvent().get(), RimoPutEvent.PASSIVE);
    linmotSafetyModule.last();
  }
}
