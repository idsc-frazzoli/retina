// code by jph
package ch.ethz.idsc.gokart.core.man;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.fuse.DavisImuTracker;
import ch.ethz.idsc.gokart.dev.ManualControlAdapter;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class DriftThrustManualModuleTest extends TestCase {
  public void testSimple() {
    DriftThrustManualModule driftThrustManualModule = new DriftThrustManualModule();
    DavisImuTracker.INSTANCE.setGyroZ(Quantity.of(0.1, SI.PER_SECOND));
    Optional<RimoPutEvent> optional = driftThrustManualModule.control(null, ManualControlAdapter.PASSIVE);
    DavisImuTracker.INSTANCE.setGyroZ(Quantity.of(0.0, SI.PER_SECOND));
    assertTrue(optional.isPresent());
    RimoPutEvent rimoPutEvent = optional.get();
    short torqueRawL = rimoPutEvent.putTireL.getTorqueRaw();
    short torqueRawR = rimoPutEvent.putTireR.getTorqueRaw();
    assertEquals(torqueRawL, -231);
    assertEquals(torqueRawR, -231);
  }

  public void testRapid() {
    DriftThrustManualModule driftThrustManualModule = new DriftThrustManualModule();
    DavisImuTracker.INSTANCE.setGyroZ(Quantity.of(1.0, SI.PER_SECOND));
    Optional<RimoPutEvent> optional = driftThrustManualModule.control(null, ManualControlAdapter.PASSIVE);
    DavisImuTracker.INSTANCE.setGyroZ(Quantity.of(0.0, SI.PER_SECOND));
    assertTrue(optional.isPresent());
    RimoPutEvent rimoPutEvent = optional.get();
    short torqueRawL = rimoPutEvent.putTireL.getTorqueRaw();
    short torqueRawR = rimoPutEvent.putTireR.getTorqueRaw();
    assertEquals(torqueRawL, -ManualConfig.GLOBAL.torqueLimit.number().shortValue());
    assertEquals(torqueRawR, -ManualConfig.GLOBAL.torqueLimit.number().shortValue());
  }

  public void testZero() {
    DriftThrustManualModule driftThrustManualModule = new DriftThrustManualModule();
    DavisImuTracker.INSTANCE.setGyroZ(Quantity.of(0.0, SI.PER_SECOND));
    Optional<RimoPutEvent> optional = driftThrustManualModule.control(null, ManualControlAdapter.PASSIVE);
    DavisImuTracker.INSTANCE.setGyroZ(Quantity.of(0.0, SI.PER_SECOND));
    assertTrue(optional.isPresent());
    RimoPutEvent rimoPutEvent = optional.get();
    short torqueRawL = rimoPutEvent.putTireL.getTorqueRaw();
    short torqueRawR = rimoPutEvent.putTireR.getTorqueRaw();
    assertEquals(torqueRawL, 0);
    assertEquals(torqueRawR, 0);
  }

  public void testForward() {
    DriftThrustManualModule driftThrustManualModule = new DriftThrustManualModule();
    DavisImuTracker.INSTANCE.setGyroZ(Quantity.of(0.0, SI.PER_SECOND));
    ManualControlInterface manualControlInterface = new ManualControlAdapter( //
        RealScalar.ZERO, RealScalar.ZERO, RealScalar.of(0.3), Tensors.vector(0, 0.3), false, false);
    Optional<RimoPutEvent> optional = driftThrustManualModule.control(null, manualControlInterface);
    DavisImuTracker.INSTANCE.setGyroZ(Quantity.of(0.0, SI.PER_SECOND));
    assertTrue(optional.isPresent());
    RimoPutEvent rimoPutEvent = optional.get();
    short torqueRawL = rimoPutEvent.putTireL.getTorqueRaw();
    short torqueRawR = rimoPutEvent.putTireR.getTorqueRaw();
    assertEquals(torqueRawL, -694);
    assertEquals(torqueRawR, +694);
  }

  public void testForwardRotate() {
    DriftThrustManualModule driftThrustManualModule = new DriftThrustManualModule();
    DavisImuTracker.INSTANCE.setGyroZ(Quantity.of(0.2, SI.PER_SECOND));
    ManualControlInterface manualControlInterface = new ManualControlAdapter( //
        RealScalar.ZERO, RealScalar.ZERO, RealScalar.of(0.3), Tensors.vector(0, 0.3), false, false);
    Optional<RimoPutEvent> optional = driftThrustManualModule.control(null, manualControlInterface);
    DavisImuTracker.INSTANCE.setGyroZ(Quantity.of(0.0, SI.PER_SECOND));
    assertTrue(optional.isPresent());
    RimoPutEvent rimoPutEvent = optional.get();
    short torqueRawL = rimoPutEvent.putTireL.getTorqueRaw();
    short torqueRawR = rimoPutEvent.putTireR.getTorqueRaw();
    assertEquals(torqueRawL, -1157);
    assertEquals(torqueRawR, +231);
  }
}
