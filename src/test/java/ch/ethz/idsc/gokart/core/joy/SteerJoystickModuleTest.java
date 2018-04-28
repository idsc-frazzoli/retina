// code by jph
package ch.ethz.idsc.gokart.core.joy;

import java.util.Optional;

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
    SteerJoystickModule sjm = new SteerJoystickModule();
    sjm.first();
    assertEquals(SteerSocket.INSTANCE.getPutProviderSize(), size + 1);
    sjm.last();
    assertEquals(SteerSocket.INSTANCE.getPutProviderSize(), size);
  }

  public void testNonCalib() {
    SteerJoystickModule sjm = new SteerJoystickModule();
    Optional<SteerPutEvent> optional = sjm.private_translate( //
        new SteerColumnAdapter(false, Quantity.of(.20, "SCE")), //
        new GokartJoystickAdapter( //
            RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(.2), Tensors.vector(0.7, 0.8), false));
    assertFalse(optional.isPresent());
    assertFalse(sjm.putEvent().isPresent()); // joystick missing
  }

  public void testCalib() {
    SteerJoystickModule sjm = new SteerJoystickModule();
    SteerColumnInterface sci = new SteerColumnAdapter(true, Quantity.of(.2, "SCE"));
    assertTrue(sci.isSteerColumnCalibrated());
    GokartJoystickInterface gji = new GokartJoystickAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(.2), Tensors.vector(0.6, 1.0), false);
    Optional<SteerPutEvent> optional = sjm.control(sci, gji);
    assertTrue(optional.isPresent());
    assertFalse(sjm.putEvent().isPresent()); // joystick missing
  }

  public void testPublic() {
    int modifs = SteerJoystickModule.class.getModifiers();
    assertEquals(modifs & 1, 1);
  }
}
