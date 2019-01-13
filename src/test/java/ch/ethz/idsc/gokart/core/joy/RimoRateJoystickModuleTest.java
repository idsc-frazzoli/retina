// code by jph
package ch.ethz.idsc.gokart.core.joy;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.GokartJoystickAdapter;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnAdapter;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
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
            RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(.2), Tensors.vector(1, 0.8), false, false));
    assertFalse(optional.isPresent());
    assertFalse(rjm.putEvent().isPresent());
  }

  public void testCalibNonGet() {
    RimoRateJoystickModule rjm = new RimoRateJoystickModule();
    SteerColumnInterface sci = new SteerColumnAdapter(true, Quantity.of(.2, "SCE"));
    assertTrue(sci.isSteerColumnCalibrated());
    ManualControlInterface gji = new GokartJoystickAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(.2), Tensors.vector(1, 0.6), false, false);
    Optional<RimoPutEvent> optional = rjm.control(sci, gji);
    assertFalse(optional.isPresent()); // no get event
  }

  public void testCalibGet() {
    RimoRateJoystickModule rjm = new RimoRateJoystickModule();
    SteerColumnInterface sci = new SteerColumnAdapter(true, Quantity.of(.2, "SCE"));
    assertTrue(sci.isSteerColumnCalibrated());
    ManualControlInterface gji = new GokartJoystickAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(.2), Tensors.vector(1, 0.5), false, false);
    RimoGetEvent rimoGetEvent = RimoGetEvents.create(-100, 200);
    rjm.rimoRateControllerWrap.getEvent(rimoGetEvent);
    Optional<RimoPutEvent> optional = rjm.control(sci, gji);
    assertTrue(optional.isPresent());
  }

  public void testTranslate() {
    RimoRateJoystickModule rjm = new RimoRateJoystickModule();
    ManualControlInterface joystick = new GokartJoystickAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(.2), Tensors.vector(1, 0.5), false, false);
    assertFalse(rjm.translate(joystick).isPresent());
  }

  public void testNonPresent() {
    RimoRateJoystickModule rtjm = new RimoRateJoystickModule();
    SteerColumnInterface steerColumnInterface = //
        new SteerColumnAdapter(false, Quantity.of(.3, SteerPutEvent.UNIT_ENCODER));
    ManualControlInterface joystick = new GokartJoystickAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(.2), Tensors.vector(1, 0.3), false, false);
    Optional<RimoPutEvent> optional = rtjm.private_translate(steerColumnInterface, joystick);
    assertFalse(optional.isPresent());
  }

  public void testStartStop() {
    RimoRateJoystickModule rtjm = new RimoRateJoystickModule();
    rtjm.protected_first();
    rtjm.protected_last();
  }
}
