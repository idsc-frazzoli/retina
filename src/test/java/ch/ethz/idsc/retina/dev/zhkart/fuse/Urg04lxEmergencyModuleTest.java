// code by jph
package ch.ethz.idsc.retina.dev.zhkart.fuse;

import junit.framework.TestCase;

public class Urg04lxEmergencyModuleTest extends TestCase {
  public void testSimple() throws Exception {
    Urg04lxEmergencyModule uem = new Urg04lxEmergencyModule();
    assertFalse(uem.putEvent().isPresent());
    Thread.sleep(420);
    assertTrue(uem.putEvent().isPresent());
  }

  public void testRank() throws Exception {
    Urg04lxEmergencyModule uem = new Urg04lxEmergencyModule();
  }
}
