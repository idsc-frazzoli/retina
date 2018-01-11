// code by jph
package ch.ethz.idsc.retina.dev.zhkart.fuse;

import junit.framework.TestCase;

public class SteerEmergencyModuleTest extends TestCase {
  public void testSimple() throws Exception {
    SteerEmergencyModule steerEmergencyModule = new SteerEmergencyModule();
    steerEmergencyModule.first();
    assertTrue(steerEmergencyModule.putEvent().isPresent());
    steerEmergencyModule.last();
  }
  // TODO test more
}
