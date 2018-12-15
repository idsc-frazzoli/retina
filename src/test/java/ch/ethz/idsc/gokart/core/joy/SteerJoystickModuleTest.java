// code by jph
package ch.ethz.idsc.gokart.core.joy;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.GokartJoystickAdapter;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.steer.SteerColumnAdapter;
import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class SteerJoystickModuleTest extends TestCase {
  public void testFirstLast() throws Exception {
    int size = SteerSocket.INSTANCE.getPutProviderSize();
    SteerJoystickModule steerJoystickModule = new SteerJoystickModule();
    steerJoystickModule.first();
    assertEquals(SteerSocket.INSTANCE.getPutProviderSize(), size + 1);
    steerJoystickModule.last();
    assertEquals(SteerSocket.INSTANCE.getPutProviderSize(), size);
  }

  public void testNonCalib() {
    SteerJoystickModule steerJoystickModule = new SteerJoystickModule();
    Optional<SteerPutEvent> optional = steerJoystickModule.private_translate( //
        new SteerColumnAdapter(false, Quantity.of(.20, "SCE")), //
        new GokartJoystickAdapter( //
            RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(.2), Tensors.vector(0.7, 0.8), false, false));
    assertFalse(optional.isPresent());
    assertFalse(steerJoystickModule.putEvent().isPresent()); // joystick missing
  }

  public void testCalib() {
    SteerJoystickModule steerJoystickModule = new SteerJoystickModule();
    SteerColumnInterface steerColumnInterface = new SteerColumnAdapter(true, Quantity.of(.2, "SCE"));
    assertTrue(steerColumnInterface.isSteerColumnCalibrated());
    GokartJoystickInterface gokartJoystickInterface = new GokartJoystickAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(.2), Tensors.vector(0.6, 1.0), false, false);
    Optional<SteerPutEvent> optional = steerJoystickModule.control(steerColumnInterface, gokartJoystickInterface);
    assertTrue(optional.isPresent());
    assertFalse(steerJoystickModule.putEvent().isPresent()); // joystick missing
  }

  public void testPublic() {
    int modifs = SteerJoystickModule.class.getModifiers();
    assertEquals(modifs & 1, 1);
  }
}
