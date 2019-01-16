// code by jph
package ch.ethz.idsc.gokart.core.joy;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.ManualControlAdapter;
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
    RimoRateJoystickModule rimoRateJoystickModule = new RimoRateJoystickModule();
    Optional<RimoPutEvent> optional = rimoRateJoystickModule.control( //
        new SteerColumnAdapter(false, Quantity.of(.20, "SCE")), //
        new ManualControlAdapter( //
            RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(.2), Tensors.vector(1, 0.8), false, false));
    assertFalse(optional.isPresent());
    assertFalse(rimoRateJoystickModule.putEvent().isPresent());
  }

  public void testCalibNonGet() {
    RimoRateJoystickModule rimoRateJoystickModule = new RimoRateJoystickModule();
    SteerColumnInterface steerColumnInterface = new SteerColumnAdapter(true, Quantity.of(.2, "SCE"));
    assertTrue(steerColumnInterface.isSteerColumnCalibrated());
    ManualControlInterface manualControlInterface = new ManualControlAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(.2), Tensors.vector(1, 0.6), false, false);
    Optional<RimoPutEvent> optional = rimoRateJoystickModule.control(steerColumnInterface, manualControlInterface);
    assertFalse(optional.isPresent()); // no get event
  }

  public void testCalibGet() {
    RimoRateJoystickModule rimoRateJoystickModule = new RimoRateJoystickModule();
    SteerColumnInterface steerColumnInterface = new SteerColumnAdapter(true, Quantity.of(.2, "SCE"));
    assertTrue(steerColumnInterface.isSteerColumnCalibrated());
    ManualControlInterface manualControlInterface = new ManualControlAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(.2), Tensors.vector(1, 0.5), false, false);
    RimoGetEvent rimoGetEvent = RimoGetEvents.create(-100, 200);
    rimoRateJoystickModule.rimoRateControllerWrap.getEvent(rimoGetEvent);
    Optional<RimoPutEvent> optional = rimoRateJoystickModule.control(steerColumnInterface, manualControlInterface);
    assertTrue(optional.isPresent());
  }

  public void testTranslate() {
    RimoRateJoystickModule rimoRateJoystickModule = new RimoRateJoystickModule();
    ManualControlInterface manualControlInterface = new ManualControlAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(.2), Tensors.vector(1, 0.5), false, false);
    assertFalse(rimoRateJoystickModule.translate(manualControlInterface).isPresent());
  }

  public void testNonPresent() {
    RimoRateJoystickModule rimoRateJoystickModule = new RimoRateJoystickModule();
    SteerColumnInterface steerColumnInterface = //
        new SteerColumnAdapter(false, Quantity.of(.3, SteerPutEvent.UNIT_ENCODER));
    ManualControlInterface manualControlInterface = new ManualControlAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(.2), Tensors.vector(1, 0.3), false, false);
    Optional<RimoPutEvent> optional = rimoRateJoystickModule.private_translate(steerColumnInterface, manualControlInterface);
    assertFalse(optional.isPresent());
  }

  public void testStartStop() {
    RimoRateJoystickModule rimoRateJoystickModule = new RimoRateJoystickModule();
    rimoRateJoystickModule.protected_first();
    rimoRateJoystickModule.protected_last();
  }
}
