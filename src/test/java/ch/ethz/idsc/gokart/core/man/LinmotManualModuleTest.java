// code by jph
package ch.ethz.idsc.gokart.core.man;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.ManualControlAdapter;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutHelper;
import ch.ethz.idsc.gokart.dev.linmot.LinmotSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class LinmotManualModuleTest extends TestCase {
  public void testFirstLast() throws Exception {
    int size = LinmotSocket.INSTANCE.getPutProviderSize();
    LinmotManualModule linmotJoystickModule = new LinmotManualModule();
    linmotJoystickModule.first();
    assertEquals(LinmotSocket.INSTANCE.getPutProviderSize(), size + 1);
    linmotJoystickModule.last();
    assertEquals(LinmotSocket.INSTANCE.getPutProviderSize(), size);
  }

  public void testSimple() {
    LinmotManualModule linmotJoystickModule = new LinmotManualModule();
    Optional<LinmotPutEvent> optional = linmotJoystickModule.putEvent();
    assertFalse(optional.isPresent());
    assertFalse(linmotJoystickModule.putEvent().isPresent());
    assertEquals(linmotJoystickModule.getProviderRank(), ProviderRank.MANUAL);
  }

  public void testValue() {
    LinmotManualModule linmotJoystickModule = new LinmotManualModule();
    ManualControlInterface manualControlInterface = new ManualControlAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(.2), Tensors.vector(1, 0.8), false, false);
    Optional<LinmotPutEvent> optional = linmotJoystickModule.translate(manualControlInterface);
    assertTrue(optional.isPresent());
    LinmotPutEvent linmotPutEvent = optional.get();
    assertEquals(linmotPutEvent.control_word, LinmotPutHelper.CMD_OPERATION.getShort());
    assertEquals(linmotPutEvent.getMotionCmdHeaderWithoutCounter(), LinmotPutHelper.MC_POSITION.getShort());
    assertEquals(linmotPutEvent.target_position, -50);
    assertEquals(linmotPutEvent.max_velocity, 1000);
    assertEquals(linmotPutEvent.acceleration, 500);
    assertEquals(linmotPutEvent.deceleration, 500);
  }

  public void testPublic() {
    int modifs = LinmotManualModule.class.getModifiers();
    assertEquals(modifs & 1, 1);
  }
}
