// code by jph
package ch.ethz.idsc.retina.dev.zhkart.joy;

import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.retina.dev.joystick.GokartJoystickAdapter;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerColumnAdapter;
import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class RimoJoystickModuleTest extends TestCase {
  public void testSimple() {
    RimoJoystickModule rjm = new RimoJoystickModule();
    Optional<RimoPutEvent> optional = rjm.control( //
        new SteerColumnAdapter(false, Quantity.of(.20, "SCE")), //
        new GokartJoystickAdapter( //
            RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(.2), Tensors.vector(1, 0.8)));
    assertFalse(optional.isPresent());
    assertFalse(rjm.putEvent().isPresent());
  }

  public void testCalibNonGet() {
    RimoJoystickModule rjm = new RimoJoystickModule();
    SteerColumnInterface sci = new SteerColumnAdapter(true, Quantity.of(.2, "SCE"));
    assertTrue(sci.isSteerColumnCalibrated());
    GokartJoystickInterface gji = new GokartJoystickAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(.2), Tensors.vector(1, 0.6));
    Optional<RimoPutEvent> optional = rjm.control(sci, gji);
    assertFalse(optional.isPresent()); // no get event
  }

  public void testCalibGet() {
    RimoJoystickModule rjm = new RimoJoystickModule();
    SteerColumnInterface sci = new SteerColumnAdapter(true, Quantity.of(.2, "SCE"));
    assertTrue(sci.isSteerColumnCalibrated());
    GokartJoystickInterface gji = new GokartJoystickAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(.2), Tensors.vector(1, 0.5));
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[48]);
    byteBuffer.putShort(2, (short) 100);
    byteBuffer.putShort(24 + 2, (short) 200); // rimogetTire.LENGTH == 24
    RimoGetEvent rimoGetEvent = new RimoGetEvent(byteBuffer);
    rjm.rimoRateControllerWrap.getEvent(rimoGetEvent);
    Optional<RimoPutEvent> optional = rjm.control(sci, gji);
    assertTrue(optional.isPresent());
  }
}
