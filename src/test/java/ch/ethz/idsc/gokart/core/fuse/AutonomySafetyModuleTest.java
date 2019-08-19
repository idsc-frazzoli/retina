// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.joystick.ManualControlAdapter;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
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

  public void testConstantFalse() throws Exception {
    ManualControlAdapter manualControlAdapter = new ManualControlAdapter(RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO, Tensors.vector(0, 0), false, false);
    AutonomySafetyModule autonomySafetyModule = new AutonomySafetyModule(new ConstantManualControlProvider(manualControlAdapter));
    autonomySafetyModule.first();
    assertTrue(autonomySafetyModule.autonomySafetyRimo.putEvent().isPresent());
    assertTrue(autonomySafetyModule.autonomySafetySteer.putEvent().isPresent());
    autonomySafetyModule.last();
  }

  public void testConstantTrue() throws Exception {
    ManualControlAdapter manualControlAdapter = new ManualControlAdapter(RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO, Tensors.vector(0, 0), true, false);
    AutonomySafetyModule autonomySafetyModule = new AutonomySafetyModule(new ConstantManualControlProvider(manualControlAdapter));
    autonomySafetyModule.first();
    assertFalse(autonomySafetyModule.autonomySafetyRimo.putEvent().isPresent());
    assertFalse(autonomySafetyModule.autonomySafetySteer.putEvent().isPresent());
    autonomySafetyModule.last();
  }
}
