// code by jph
package ch.ethz.idsc.gokart.dev;

import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClientTest;
import junit.framework.TestCase;

public class HybridControlProviderTest extends TestCase {
  public void testSimple() throws InterruptedException {
    HybridControlProvider hybridControlProvider = new HybridControlProvider();
    assertFalse(hybridControlProvider.getJoystick().isPresent());
    hybridControlProvider.start();
    assertFalse(hybridControlProvider.getJoystick().isPresent());
    JoystickLcmClientTest.publishOne();
    Thread.sleep(20);
    assertTrue(hybridControlProvider.getJoystick().isPresent());
    hybridControlProvider.stop();
    hybridControlProvider.stop();
  }
}
