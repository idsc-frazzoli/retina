// code by jph
package ch.ethz.idsc.gokart.core.joy;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerColumnAdapter;
import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class RimoRateJoystickModuleTest extends TestCase {
  public void testSimple() {
    RimoRateJoystickModule rjm = new RimoRateJoystickModule();
    Optional<RimoPutEvent> optional = rjm.control( //
        new SteerColumnAdapter(false, Quantity.of(.20, "SCE")), //
        new GokartJoystickAdapter( //
            RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(.2), Tensors.vector(1, 0.8), false));
    assertFalse(optional.isPresent());
    assertFalse(rjm.putEvent().isPresent());
  }

  public void testCalibNonGet() {
    RimoRateJoystickModule rjm = new RimoRateJoystickModule();
    SteerColumnInterface sci = new SteerColumnAdapter(true, Quantity.of(.2, "SCE"));
    assertTrue(sci.isSteerColumnCalibrated());
    GokartJoystickInterface gji = new GokartJoystickAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(.2), Tensors.vector(1, 0.6), false);
    Optional<RimoPutEvent> optional = rjm.control(sci, gji);
    assertFalse(optional.isPresent()); // no get event
  }

  public void testCalibGet() {
    RimoRateJoystickModule rjm = new RimoRateJoystickModule();
    SteerColumnInterface sci = new SteerColumnAdapter(true, Quantity.of(.2, "SCE"));
    assertTrue(sci.isSteerColumnCalibrated());
    GokartJoystickInterface gji = new GokartJoystickAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(.2), Tensors.vector(1, 0.5), false);
    RimoGetEvent rimoGetEvent = RimoGetEvents.create(-100, 200);
    rjm.rimoRateControllerWrap.getEvent(rimoGetEvent);
    Optional<RimoPutEvent> optional = rjm.control(sci, gji);
    assertTrue(optional.isPresent());
  }

  public void testTranslate() {
    RimoRateJoystickModule rjm = new RimoRateJoystickModule();
    GokartJoystickInterface joystick = new GokartJoystickAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(.2), Tensors.vector(1, 0.5), false);
    assertFalse(rjm.translate(joystick).isPresent());
  }

  public void testNonPresent() {
    RimoRateJoystickModule rtjm = new RimoRateJoystickModule();
    SteerColumnInterface steerColumnInterface = //
        new SteerColumnAdapter(false, Quantity.of(.3, SteerPutEvent.UNIT_ENCODER));
    GokartJoystickInterface joystick = new GokartJoystickAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(.2), Tensors.vector(1, 0.3), false);
    Optional<RimoPutEvent> optional = rtjm.private_translate(steerColumnInterface, joystick);
    assertFalse(optional.isPresent());
  }

  public void testStartStop() {
    RimoRateJoystickModule rtjm = new RimoRateJoystickModule();
    rtjm.protected_first();
    rtjm.protected_last();
  }
}
