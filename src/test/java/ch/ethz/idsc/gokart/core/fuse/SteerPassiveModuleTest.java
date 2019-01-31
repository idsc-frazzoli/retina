// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.gokart.dev.linmot.LinmotGetHelper;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import junit.framework.TestCase;

public class SteerPassiveModuleTest extends TestCase {
  public void testSimple() throws Exception {
    SteerPassiveModule steerPassiveModule = new SteerPassiveModule();
    steerPassiveModule.first();
    assertEquals(steerPassiveModule.getProviderRank(), ProviderRank.SAFETY);
    assertFalse(steerPassiveModule.putEvent().isPresent());
    steerPassiveModule.getEvent(LinmotGetHelper.createNonOperational(-23545));
    assertFalse(steerPassiveModule.putEvent().isPresent());
    steerPassiveModule.getEvent(LinmotGetHelper.createNonOperational(-400000));
    assertTrue(steerPassiveModule.putEvent().isPresent());
    steerPassiveModule.getEvent(LinmotGetHelper.createNonOperational(-23545));
    assertTrue(steerPassiveModule.putEvent().isPresent());
    steerPassiveModule.getEvent(LinmotGetHelper.createPos(-23545, -23545));
    assertFalse(steerPassiveModule.putEvent().isPresent());
    steerPassiveModule.last();
  }
}
