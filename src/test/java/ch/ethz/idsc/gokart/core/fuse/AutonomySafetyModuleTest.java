// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import junit.framework.TestCase;

public class AutonomySafetyModuleTest extends TestCase {
  public void testSimple() throws Exception {
    int countPutRimo = RimoSocket.INSTANCE.getPutProviderSize();
    int countPutSteer = SteerSocket.INSTANCE.getPutProviderSize();
    AutonomySafetyModule autonomySafetyModule = new AutonomySafetyModule();
    autonomySafetyModule.first();
    assertEquals(countPutRimo + 1, RimoSocket.INSTANCE.getPutProviderSize());
    assertEquals(countPutSteer + 1, SteerSocket.INSTANCE.getPutProviderSize());
    autonomySafetyModule.last();
    assertEquals(countPutRimo, RimoSocket.INSTANCE.getPutProviderSize());
    assertEquals(countPutSteer, SteerSocket.INSTANCE.getPutProviderSize());
  }
}
