// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
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

  public void testMore() throws Exception {
    AutonomySafetyModule autonomySafetyModule = new AutonomySafetyModule();
    autonomySafetyModule.first();
    assertEquals(autonomySafetyModule.autonomySafetyRimo.getProviderRank(), ProviderRank.SAFETY);
    assertEquals(autonomySafetyModule.autonomySafetySteer.getProviderRank(), ProviderRank.SAFETY);
    assertTrue(autonomySafetyModule.autonomySafetyRimo.putEvent().isPresent());
    assertTrue(autonomySafetyModule.autonomySafetySteer.putEvent().isPresent());
    autonomySafetyModule.last();
  }
}
