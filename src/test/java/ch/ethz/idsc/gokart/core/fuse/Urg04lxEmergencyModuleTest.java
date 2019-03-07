// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.owl.ani.api.ProviderRank;
import junit.framework.TestCase;

public class Urg04lxEmergencyModuleTest extends TestCase {
  public void testFirstLast() throws Exception {
    Urg04lxEmergencyModule uem = new Urg04lxEmergencyModule();
    uem.first();
    uem.last();
  }

  public void testSimple() throws Exception {
    Urg04lxEmergencyModule uem = new Urg04lxEmergencyModule();
    assertFalse(uem.putEvent().isPresent());
    Thread.sleep(420);
    assertTrue(uem.putEvent().isPresent());
  }

  public void testRank() throws Exception {
    Urg04lxEmergencyModule uem = new Urg04lxEmergencyModule();
    assertEquals(uem.getProviderRank(), ProviderRank.EMERGENCY);
  }

  public void testNominal() throws Exception {
    Urg04lxEmergencyModule uem = new Urg04lxEmergencyModule();
    assertFalse(uem.putEvent().isPresent());
    Thread.sleep(100);
    uem.timestamp(1, 1);
    uem.scan(1, null);
    Thread.sleep(100);
    uem.timestamp(1, 1);
    Thread.sleep(100);
    uem.timestamp(1, 1);
    Thread.sleep(100);
    uem.timestamp(1, 1);
    Thread.sleep(100);
    uem.timestamp(1, 1);
    assertFalse(uem.putEvent().isPresent());
  }
}
