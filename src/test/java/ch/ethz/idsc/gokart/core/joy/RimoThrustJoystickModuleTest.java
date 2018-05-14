// code by jph
package ch.ethz.idsc.gokart.core.joy;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutTire;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerColumnAdapter;
import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class RimoThrustJoystickModuleTest extends TestCase {
  public void testFirstLast() throws Exception {
    int size = RimoSocket.INSTANCE.getPutProviderSize();
    RimoThrustJoystickModule rtjm = new RimoThrustJoystickModule();
    rtjm.first();
    assertEquals(RimoSocket.INSTANCE.getPutProviderSize(), size + 1);
    rtjm.last();
    assertEquals(RimoSocket.INSTANCE.getPutProviderSize(), size);
  }

  public void testSimple() {
    RimoThrustJoystickModule rtjm = new RimoThrustJoystickModule();
    SteerColumnInterface steerColumnInterface = //
        new SteerColumnAdapter(true, Quantity.of(.3, SteerPutEvent.UNIT_ENCODER));
    GokartJoystickInterface joystick = new GokartJoystickAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(.2), Tensors.vector(0, .5), false);
    Optional<RimoPutEvent> optional = rtjm.control(steerColumnInterface, joystick);
    assertTrue(optional.isPresent());
    RimoPutEvent rimoPutEvent = optional.get();
    RimoPutTire rptL = rimoPutEvent.putTireL;
    RimoPutTire rptR = rimoPutEvent.putTireR;
    JoystickConfig.GLOBAL.torqueLimitClip().isInside(rptL.getTorque());
    JoystickConfig.GLOBAL.torqueLimitClip().isInside(rptR.getTorque());
    // ---
    short rptL_raw = rptL.getTorqueRaw();
    short rptR_raw = rptR.getTorqueRaw();
    assertTrue(rptL_raw < 0);
    assertEquals(rptL_raw, -rptR_raw);
    int expected = JoystickConfig.GLOBAL.torqueLimit.number().intValue() / 2;
    assertEquals(rptR_raw, expected);
  }

  public void testFull() {
    RimoThrustJoystickModule rtjm = new RimoThrustJoystickModule();
    SteerColumnInterface steerColumnInterface = //
        new SteerColumnAdapter(true, Quantity.of(.3, SteerPutEvent.UNIT_ENCODER));
    GokartJoystickInterface joystick = new GokartJoystickAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(1), Tensors.vector(0, 1), false);
    Optional<RimoPutEvent> optional = rtjm.control(steerColumnInterface, joystick);
    assertTrue(optional.isPresent());
    RimoPutEvent rimoPutEvent = optional.get();
    RimoPutTire rptL = rimoPutEvent.putTireL;
    RimoPutTire rptR = rimoPutEvent.putTireR;
    JoystickConfig.GLOBAL.torqueLimitClip().isInside(rptL.getTorque());
    JoystickConfig.GLOBAL.torqueLimitClip().isInside(rptR.getTorque());
    // ---
    short rptL_raw = rptL.getTorqueRaw();
    short rptR_raw = rptR.getTorqueRaw();
    assertTrue(rptL_raw < 0);
    assertEquals(rptL_raw, -rptR_raw);
    int expected = JoystickConfig.GLOBAL.torqueLimit.number().intValue();
    assertEquals(rptR_raw, expected);
  }

  public void testFullReverse() {
    RimoThrustJoystickModule rtjm = new RimoThrustJoystickModule();
    SteerColumnInterface steerColumnInterface = //
        new SteerColumnAdapter(true, Quantity.of(.3, SteerPutEvent.UNIT_ENCODER));
    GokartJoystickInterface joystick = new GokartJoystickAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(1), Tensors.vector(1, 0), false);
    Optional<RimoPutEvent> optional = rtjm.control(steerColumnInterface, joystick);
    assertTrue(optional.isPresent());
    RimoPutEvent rimoPutEvent = optional.get();
    RimoPutTire rptL = rimoPutEvent.putTireL;
    RimoPutTire rptR = rimoPutEvent.putTireR;
    JoystickConfig.GLOBAL.torqueLimitClip().isInside(rptL.getTorque());
    JoystickConfig.GLOBAL.torqueLimitClip().isInside(rptR.getTorque());
    // ---
    short rptL_raw = rptL.getTorqueRaw();
    short rptR_raw = rptR.getTorqueRaw();
    assertTrue(rptL_raw > 0);
    assertEquals(rptL_raw, -rptR_raw);
    int expected = -JoystickConfig.GLOBAL.torqueLimit.number().intValue();
    assertEquals(rptR_raw, expected);
  }

  public void testReverse() {
    RimoThrustJoystickModule rtjm = new RimoThrustJoystickModule();
    SteerColumnInterface steerColumnInterface = //
        new SteerColumnAdapter(true, Quantity.of(.3, SteerPutEvent.UNIT_ENCODER));
    GokartJoystickInterface joystick = new GokartJoystickAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(-1), Tensors.vector(0, 0), false);
    Optional<RimoPutEvent> optional = rtjm.control(steerColumnInterface, joystick);
    assertTrue(optional.isPresent());
    RimoPutEvent rimoPutEvent = optional.get();
    RimoPutTire rptL = rimoPutEvent.putTireL;
    RimoPutTire rptR = rimoPutEvent.putTireR;
    JoystickConfig.GLOBAL.torqueLimitClip().isInside(rptL.getTorque());
    JoystickConfig.GLOBAL.torqueLimitClip().isInside(rptR.getTorque());
    // ---
    short rptL_raw = rptL.getTorqueRaw();
    short rptR_raw = rptR.getTorqueRaw();
    assertEquals(rptL_raw, 0);
    assertEquals(rptR_raw, 0);
  }

  public void testReverseForward() {
    RimoThrustJoystickModule rtjm = new RimoThrustJoystickModule();
    SteerColumnInterface steerColumnInterface = //
        new SteerColumnAdapter(true, Quantity.of(.3, SteerPutEvent.UNIT_ENCODER));
    GokartJoystickInterface joystick = new GokartJoystickAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(-1), Tensors.vector(0, 1), false);
    Optional<RimoPutEvent> optional = rtjm.control(steerColumnInterface, joystick);
    assertTrue(optional.isPresent());
    RimoPutEvent rimoPutEvent = optional.get();
    RimoPutTire rptL = rimoPutEvent.putTireL;
    RimoPutTire rptR = rimoPutEvent.putTireR;
    JoystickConfig.GLOBAL.torqueLimitClip().isInside(rptL.getTorque());
    JoystickConfig.GLOBAL.torqueLimitClip().isInside(rptR.getTorque());
    // ---
    short rptL_raw = rptL.getTorqueRaw();
    short rptR_raw = rptR.getTorqueRaw();
    int expected = JoystickConfig.GLOBAL.torqueLimit.number().intValue();
    assertEquals(rptL_raw, -expected);
    assertEquals(rptL_raw, -rptR_raw);
  }

  public void testNonPresent() {
    RimoThrustJoystickModule rtjm = new RimoThrustJoystickModule();
    SteerColumnInterface steerColumnInterface = //
        new SteerColumnAdapter(false, Quantity.of(.3, SteerPutEvent.UNIT_ENCODER));
    GokartJoystickInterface joystick = new GokartJoystickAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(.2), Tensors.vector(1, 0.3), false);
    Optional<RimoPutEvent> optional = rtjm.private_translate(steerColumnInterface, joystick);
    assertFalse(optional.isPresent());
  }

  public void testPublic() {
    int modifs = RimoThrustJoystickModule.class.getModifiers();
    assertEquals(modifs & 1, 1);
  }
}
