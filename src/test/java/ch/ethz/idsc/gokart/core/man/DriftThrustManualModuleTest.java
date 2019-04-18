// code by jph
package ch.ethz.idsc.gokart.core.man;

import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class DriftThrustManualModuleTest extends TestCase {
  public void testSimple() throws Exception {
    ModuleAuto.INSTANCE.runOne(LidarLocalizationModule.class);
    DriftThrustManualModule driftThrustManualModule = new DriftThrustManualModule();
    RimoPutEvent rimoPutEvent = driftThrustManualModule.derive( //
        RealScalar.ZERO, Quantity.of(0.1, SI.PER_SECOND), RealScalar.ZERO);
    short torqueRawL = rimoPutEvent.putTireL.getTorqueRaw();
    short torqueRawR = rimoPutEvent.putTireR.getTorqueRaw();
    assertEquals(torqueRawL, 463);
    assertEquals(torqueRawR, 463);
    ModuleAuto.INSTANCE.endOne(LidarLocalizationModule.class);
  }

  public void testRapid() throws Exception {
    ModuleAuto.INSTANCE.runOne(LidarLocalizationModule.class);
    DriftThrustManualModule driftThrustManualModule = new DriftThrustManualModule();
    RimoPutEvent rimoPutEvent = driftThrustManualModule.derive( //
        RealScalar.ZERO, Quantity.of(1.0, SI.PER_SECOND), RealScalar.ZERO);
    short torqueRawL = rimoPutEvent.putTireL.getTorqueRaw();
    short torqueRawR = rimoPutEvent.putTireR.getTorqueRaw();
    assertEquals(torqueRawL, ManualConfig.GLOBAL.torqueLimit.number().shortValue());
    assertEquals(torqueRawR, ManualConfig.GLOBAL.torqueLimit.number().shortValue());
    ModuleAuto.INSTANCE.endOne(LidarLocalizationModule.class);
  }

  public void testZero() throws Exception {
    ModuleAuto.INSTANCE.runOne(LidarLocalizationModule.class);
    DriftThrustManualModule driftThrustManualModule = new DriftThrustManualModule();
    RimoPutEvent rimoPutEvent = driftThrustManualModule.derive( //
        RealScalar.of(0.0), Quantity.of(0.0, SI.PER_SECOND), RealScalar.ZERO);
    short torqueRawL = rimoPutEvent.putTireL.getTorqueRaw();
    short torqueRawR = rimoPutEvent.putTireR.getTorqueRaw();
    assertEquals(torqueRawL, 0);
    assertEquals(torqueRawR, 0);
    ModuleAuto.INSTANCE.endOne(LidarLocalizationModule.class);
  }

  public void testForward() throws Exception {
    ModuleAuto.INSTANCE.runOne(LidarLocalizationModule.class);
    DriftThrustManualModule driftThrustManualModule = new DriftThrustManualModule();
    RimoPutEvent rimoPutEvent = driftThrustManualModule.derive( //
        RealScalar.of(0.3), Quantity.of(0.0, SI.PER_SECOND), RealScalar.ZERO);
    short torqueRawL = rimoPutEvent.putTireL.getTorqueRaw();
    short torqueRawR = rimoPutEvent.putTireR.getTorqueRaw();
    assertEquals(torqueRawL, -694);
    assertEquals(torqueRawR, +694);
    ModuleAuto.INSTANCE.endOne(LidarLocalizationModule.class);
  }

  public void testForwardRotate() throws Exception {
    ModuleAuto.INSTANCE.runOne(LidarLocalizationModule.class);
    DriftThrustManualModule driftThrustManualModule = new DriftThrustManualModule();
    RimoPutEvent rimoPutEvent = driftThrustManualModule.derive( //
        RealScalar.of(0.3), Quantity.of(0.2, SI.PER_SECOND), RealScalar.ZERO);
    short torqueRawL = rimoPutEvent.putTireL.getTorqueRaw();
    short torqueRawR = rimoPutEvent.putTireR.getTorqueRaw();
    assertEquals(torqueRawL, +231);
    assertEquals(torqueRawR, 1620);
    ModuleAuto.INSTANCE.endOne(LidarLocalizationModule.class);
  }
}
