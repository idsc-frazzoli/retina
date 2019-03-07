// code by jph
package ch.ethz.idsc.gokart.core.man;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.ManualControlAdapter;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnAdapter;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class SteerManualModuleTest extends TestCase {
  public void testFirstLast() throws Exception {
    int size = SteerSocket.INSTANCE.getPutProviderSize();
    SteerManualModule steerJoystickModule = new SteerManualModule();
    steerJoystickModule.first();
    assertEquals(SteerSocket.INSTANCE.getPutProviderSize(), size + 1);
    steerJoystickModule.last();
    assertEquals(SteerSocket.INSTANCE.getPutProviderSize(), size);
  }

  public void testNonCalib() {
    SteerManualModule steerJoystickModule = new SteerManualModule();
    Optional<SteerPutEvent> optional = steerJoystickModule.private_translate( //
        new SteerColumnAdapter(false, Quantity.of(.20, "SCE")), //
        new ManualControlAdapter( //
            RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(.2), Tensors.vector(0.7, 0.8), false, false));
    assertFalse(optional.isPresent());
    assertFalse(steerJoystickModule.putEvent().isPresent()); // joystick missing
  }

  public void testCalib() {
    SteerManualModule steerJoystickModule = new SteerManualModule();
    SteerColumnInterface steerColumnInterface = new SteerColumnAdapter(true, Quantity.of(.2, "SCE"));
    assertTrue(steerColumnInterface.isSteerColumnCalibrated());
    ManualControlInterface manualControlInterface = new ManualControlAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(.2), Tensors.vector(0.6, 1.0), false, false);
    Optional<SteerPutEvent> optional = steerJoystickModule.control(steerColumnInterface, manualControlInterface);
    assertTrue(optional.isPresent());
    assertFalse(steerJoystickModule.putEvent().isPresent()); // joystick missing
  }

  public void testPublic() {
    int modifs = SteerManualModule.class.getModifiers();
    assertEquals(modifs & 1, 1);
  }
}
