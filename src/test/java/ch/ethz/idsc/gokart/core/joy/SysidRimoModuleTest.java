// code by jph
package ch.ethz.idsc.gokart.core.joy;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class SysidRimoModuleTest extends TestCase {
  public void testSimple() throws Exception {
    SysidRimoModule sysidRimoModule = new SysidRimoModule();
    sysidRimoModule.first();
    Optional<RimoPutEvent> optional = sysidRimoModule.putEvent();
    assertFalse(optional.isPresent());
    sysidRimoModule.last();
  }

  public void testValue() throws Exception {
    SysidRimoModule sysidRimoModule = new SysidRimoModule();
    sysidRimoModule.setSignal(SysidSignals.PRBS7_SIGNED_FAST.get());
    {
      RimoPutEvent rpe = sysidRimoModule.create(RealScalar.of(.6), DoubleScalar.of(1.5));
      assertEquals(rpe.getTorque_Y_pair(), Tensors.fromString("{-900[ARMS], -900[ARMS]}"));
    }
    {
      RimoPutEvent rpe = sysidRimoModule.create(RealScalar.of(.6), DoubleScalar.of(3.5));
      assertEquals(rpe.getTorque_Y_pair(), Tensors.fromString("{900[ARMS], 900[ARMS]}"));
    }
  }

  public void testJoystickPresent() {
    SysidRimoModule sysidRimoModule = new SysidRimoModule();
    GokartJoystickInterface gji = //
        new GokartJoystickAdapter(RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO, Tensors.vector(0, 0), true);
    Optional<RimoPutEvent> optional = sysidRimoModule.fromJoystick(gji);
    assertTrue(optional.isPresent());
  }

  public void testJoystickNotPresent() {
    SysidRimoModule sysidRimoModule = new SysidRimoModule();
    GokartJoystickInterface gji = //
        new GokartJoystickAdapter(RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO, Tensors.vector(0, 0), false);
    Optional<RimoPutEvent> optional = sysidRimoModule.fromJoystick(gji);
    assertFalse(optional.isPresent());
  }

  public void testMultiSubscribe() throws Exception {
    SysidRimoModule sysidRimoModule = new SysidRimoModule();
    for (int count = 0; count < 10; ++count) {
      sysidRimoModule.first();
      sysidRimoModule.last();
    }
  }
}
