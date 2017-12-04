// code by jph
package ch.ethz.idsc.retina.dev.zhkart.joy;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.joystick.GokartJoystickAdapter;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.steer.SteerColumnAdapter;
import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class SteerJoystickModuleTest extends TestCase {
  public void testNonCalib() {
    SteerJoystickModule sjm = new SteerJoystickModule();
    Optional<SteerPutEvent> optional = sjm.control( //
        new SteerColumnAdapter(false, Quantity.of(.20, "SCE")), //
        new GokartJoystickAdapter( //
            RealScalar.of(.1), 0.0, RealScalar.of(.2), Tensors.vector(1, 2)));
    assertFalse(optional.isPresent());
    assertFalse(sjm.putEvent().isPresent()); // joystick missing
  }

  public void testCalib() {
    SteerJoystickModule sjm = new SteerJoystickModule();
    SteerColumnInterface sci = new SteerColumnAdapter(true, Quantity.of(.2, "SCE"));
    assertTrue(sci.isSteerColumnCalibrated());
    GokartJoystickInterface gji = new GokartJoystickAdapter( //
        RealScalar.of(.1), 0.0, RealScalar.of(.2), Tensors.vector(1, 2));
    Optional<SteerPutEvent> optional = sjm.control(sci, gji);
    assertTrue(optional.isPresent());
    assertFalse(sjm.putEvent().isPresent()); // joystick missing
  }
}
