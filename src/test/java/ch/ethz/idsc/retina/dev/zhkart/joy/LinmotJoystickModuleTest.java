// code by jph
package ch.ethz.idsc.retina.dev.zhkart.joy;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.joystick.GokartJoystickAdapter;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutHelper;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class LinmotJoystickModuleTest extends TestCase {
  public void testSimple() {
    LinmotJoystickModule linmotJoystickModule = new LinmotJoystickModule();
    Optional<LinmotPutEvent> optional = linmotJoystickModule.putEvent();
    assertFalse(optional.isPresent());
    assertFalse(linmotJoystickModule.putEvent().isPresent());
    assertEquals(linmotJoystickModule.getProviderRank(), ProviderRank.MANUAL);
  }

  public void testValue() {
    LinmotJoystickModule linmotJoystickModule = new LinmotJoystickModule();
    GokartJoystickAdapter joystick = new GokartJoystickAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(.2), Tensors.vector(1, 0.8));
    Optional<LinmotPutEvent> optional = linmotJoystickModule.translate(joystick);
    assertTrue(optional.isPresent());
    LinmotPutEvent linmotPutEvent = optional.get();
    assertEquals(linmotPutEvent.control_word, LinmotPutHelper.CMD_OPERATION.getShort());
    assertEquals(linmotPutEvent.motion_cmd_hdr, LinmotPutHelper.MC_POSITION.getShort());
    assertEquals(linmotPutEvent.target_position, -50);
    assertEquals(linmotPutEvent.max_velocity, 1000);
    assertEquals(linmotPutEvent.acceleration, 500);
    assertEquals(linmotPutEvent.deceleration, 500);
  }
}
