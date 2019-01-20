// code by mh, jph
package ch.ethz.idsc.gokart.core.man;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.fuse.DavisImuTracker;
import ch.ethz.idsc.gokart.dev.ManualControlAdapter;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnAdapter;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class TorqueVectoringManualModuleTest extends TestCase {
  public void testSimple() throws Exception {
    TorqueVectoringManualModule torqueVectoringJoystickModule = new SimpleTorqueVectoringManualModule();
    torqueVectoringJoystickModule.first();
    torqueVectoringJoystickModule.last();
  }

  public void testControl() throws Exception {
    TorqueVectoringManualModule torqueVectoringJoystickModule = new SimpleTorqueVectoringManualModule();
    torqueVectoringJoystickModule.first();
    DavisImuTracker.INSTANCE.setGyroZ(Quantity.of(0.0, SI.PER_SECOND));
    torqueVectoringJoystickModule.getEvent(RimoGetEvents.create(100, 200));
    SteerColumnAdapter steerColumnAdapter = new SteerColumnAdapter(true, Quantity.of(0, "SCE"));
    ManualControlInterface manualControlInterface = new ManualControlAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(0), Tensors.vector(0, 0), false, false);
    Optional<RimoPutEvent> control = torqueVectoringJoystickModule.control(steerColumnAdapter, manualControlInterface);
    RimoPutEvent rimoPutEvent1 = control.get();
    assertEquals(rimoPutEvent1.putTireL.getTorque(), Quantity.of(0, NonSI.ARMS));
    assertEquals(rimoPutEvent1.putTireR.getTorque(), Quantity.of(0, NonSI.ARMS));
    // full forward
    DavisImuTracker.INSTANCE.setGyroZ(Quantity.of(0, SI.PER_SECOND));
    torqueVectoringJoystickModule.getEvent(RimoGetEvents.create(200, 200));
    steerColumnAdapter = new SteerColumnAdapter(true, Quantity.of(0, "SCE"));
    manualControlInterface = new ManualControlAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(0), Tensors.vector(0, 1), false, false);
    control = torqueVectoringJoystickModule.control(steerColumnAdapter, manualControlInterface);
    RimoPutEvent rimoPutEvent2 = control.get();
    assertEquals(rimoPutEvent2.putTireL.getTorque(), ManualConfig.GLOBAL.torqueLimit.negate());
    assertEquals(rimoPutEvent2.putTireR.getTorque(), ManualConfig.GLOBAL.torqueLimit);
    // half forward slip right
    torqueVectoringJoystickModule.last();
  }

  public void testControl2() throws Exception {
    TorqueVectoringManualModule torqueVectoringJoystickModule = new SimpleTorqueVectoringManualModule();
    torqueVectoringJoystickModule.first();
    Scalar slip = RationalScalar.HALF; // 1/2 forward slip right
    DavisImuTracker.INSTANCE.setGyroZ(Quantity.of(-0.2, SI.PER_SECOND));
    torqueVectoringJoystickModule.getEvent(RimoGetEvents.create(200, 200));
    SteerColumnAdapter steerColumnAdapter = new SteerColumnAdapter(true, Quantity.of(0.1, "SCE"));
    ManualControlInterface manualControlInterface = new ManualControlAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(0), Tensors.vector(0, 0.5), false, false);
    Optional<RimoPutEvent> control = torqueVectoringJoystickModule.control(steerColumnAdapter, manualControlInterface);
    RimoPutEvent rimoPutEvent3 = control.get();
    // FIXME: why did I have to change that value
    Clip.function(Quantity.of(-400, "ARMS"), Quantity.of(-300, "ARMS")).requireInside(rimoPutEvent3.putTireL.getTorque());
    Clip.function(Quantity.of(1900, "ARMS"), Quantity.of(2000, "ARMS")).requireInside(rimoPutEvent3.putTireR.getTorque());
    assertTrue(Scalars.lessThan(rimoPutEvent3.putTireL.getTorque().negate(), rimoPutEvent3.putTireR.getTorque()));
    Sign.requirePositive(rimoPutEvent3.putTireR.getTorque());
    Scalar meanPower = rimoPutEvent3.putTireL.getTorque().negate().add(rimoPutEvent3.putTireR.getTorque()).divide(Quantity.of(2, SI.ONE));
    Scalar wantedPower = ManualConfig.GLOBAL.torqueLimit.multiply(slip);
    assertTrue(Scalars.lessThan(meanPower.subtract(wantedPower).abs(), Quantity.of(1, NonSI.ARMS)));
    torqueVectoringJoystickModule.last();
  }

  public void testControl3() throws Exception {
    TorqueVectoringManualModule torqueVectoringJoystickModule = new SimpleTorqueVectoringManualModule();
    torqueVectoringJoystickModule.first();
    Scalar slip = RationalScalar.of(3, 4); // 3/4 forward slip right
    DavisImuTracker.INSTANCE.setGyroZ(Quantity.of(-0.3, SI.PER_SECOND));
    torqueVectoringJoystickModule.getEvent(RimoGetEvents.create(200, 200));
    SteerColumnAdapter steerColumnAdapter = new SteerColumnAdapter(true, Quantity.of(0.1, "SCE"));
    ManualControlInterface manualControlInterface = new ManualControlAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(0), Tensors.vector(0, 0.75), false, false);
    Optional<RimoPutEvent> control = torqueVectoringJoystickModule.control(steerColumnAdapter, manualControlInterface);
    RimoPutEvent rimoPutEvent4 = control.get();
    Clip.function(Quantity.of(-1200, "ARMS"), Quantity.of(-1100, "ARMS")).requireInside(rimoPutEvent4.putTireL.getTorque());
    Clip.function(Quantity.of(+2300, "ARMS"), Quantity.of(+2315, "ARMS")).requireInside(rimoPutEvent4.putTireR.getTorque());
    assertTrue(Scalars.lessThan(rimoPutEvent4.putTireL.getTorque().negate(), rimoPutEvent4.putTireR.getTorque()));
    assertTrue(Scalars.lessThan(Quantity.of(0, NonSI.ARMS), rimoPutEvent4.putTireR.getTorque()));
    Scalar meanPower = rimoPutEvent4.putTireL.getTorque().negate().add(rimoPutEvent4.putTireR.getTorque()).divide(Quantity.of(2, SI.ONE));
    Scalar wantedPower = ManualConfig.GLOBAL.torqueLimit.multiply(slip);
    assertTrue(Scalars.lessThan(meanPower.subtract(wantedPower).abs(), Quantity.of(1, NonSI.ARMS)));
    torqueVectoringJoystickModule.last();
  }

  public void testControl4() throws Exception {
    TorqueVectoringManualModule torqueVectoringJoystickModule = new SimpleTorqueVectoringManualModule();
    torqueVectoringJoystickModule.first();
    Scalar slip = RationalScalar.of(3, 4); // 3/4 forward slip left
    System.out.println(slip + " slip left");
    DavisImuTracker.INSTANCE.setGyroZ(Quantity.of(0.3, SI.PER_SECOND));
    torqueVectoringJoystickModule.getEvent(RimoGetEvents.create(200, 200));
    SteerColumnAdapter steerColumnAdapter = new SteerColumnAdapter(true, Quantity.of(-0.1, "SCE"));
    ManualControlInterface manualControlInterface = new ManualControlAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(0), Tensors.vector(0, 0.75), false, false);
    Optional<RimoPutEvent> control = torqueVectoringJoystickModule.control(steerColumnAdapter, manualControlInterface);
    RimoPutEvent rimoPutEvent5 = control.get();
    System.out.println(rimoPutEvent5.putTireL.getTorque());
    System.out.println(rimoPutEvent5.putTireR.getTorque());
    assertTrue(Scalars.lessThan(rimoPutEvent5.putTireR.getTorque(), rimoPutEvent5.putTireL.getTorque().negate()));
    assertTrue(Scalars.lessThan(Quantity.of(0, NonSI.ARMS), rimoPutEvent5.putTireL.getTorque().negate()));
    Scalar meanPower = rimoPutEvent5.putTireL.getTorque().negate().add(rimoPutEvent5.putTireR.getTorque()).divide(Quantity.of(2, SI.ONE));
    Scalar wantedPower = ManualConfig.GLOBAL.torqueLimit.multiply(slip);
    assertTrue(Scalars.lessThan(meanPower.subtract(wantedPower).abs(), Quantity.of(1, NonSI.ARMS)));
    torqueVectoringJoystickModule.last();
  }

  public void testControl5() throws Exception {
    TorqueVectoringManualModule tvjm = new SimpleTorqueVectoringManualModule();
    tvjm.first();
    Scalar slip = RationalScalar.of(-3, 4); // 3/4 forward slip right
    System.out.println(slip + " slip right");
    DavisImuTracker.INSTANCE.setGyroZ(Quantity.of(-0.3, SI.PER_SECOND));
    tvjm.getEvent(RimoGetEvents.create(200, 200));
    SteerColumnAdapter steerColumnAdapter = new SteerColumnAdapter(true, Quantity.of(0.1, "SCE"));
    ManualControlInterface manualControlInterface = new ManualControlAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(0), Tensors.vector(0.75, 0), false, false);
    Optional<RimoPutEvent> control = tvjm.control(steerColumnAdapter, manualControlInterface);
    RimoPutEvent rimoPutEvent6 = control.get();
    System.out.println(rimoPutEvent6.putTireL.getTorque());
    System.out.println(rimoPutEvent6.putTireR.getTorque());
    assertTrue(Scalars.lessThan(rimoPutEvent6.putTireL.getTorque().negate(), rimoPutEvent6.putTireR.getTorque()));
    Scalar meanPower = rimoPutEvent6.putTireL.getTorque().negate().add(rimoPutEvent6.putTireR.getTorque()).divide(Quantity.of(2, SI.ONE));
    Scalar wantedPower = ManualConfig.GLOBAL.torqueLimit.multiply(slip);
    assertTrue(Scalars.lessThan(meanPower.subtract(wantedPower).abs(), Quantity.of(1, NonSI.ARMS)));
    tvjm.last();
  }

  public void testControl6() throws Exception {
    TorqueVectoringManualModule torqueVectoringJoystickModule = new SimpleTorqueVectoringManualModule();
    torqueVectoringJoystickModule.first();
    Scalar slip = RationalScalar.of(-3, 4); // 3/4 forward slip left
    System.out.println(slip + " slip left");
    DavisImuTracker.INSTANCE.setGyroZ(Quantity.of(0.3, SI.PER_SECOND));
    torqueVectoringJoystickModule.getEvent(RimoGetEvents.create(200, 200));
    SteerColumnAdapter steerColumnAdapter = new SteerColumnAdapter(true, Quantity.of(-0.1, "SCE"));
    ManualControlInterface manualControlInterface = new ManualControlAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(0), Tensors.vector(0.75, 0), false, false);
    Optional<RimoPutEvent> control = torqueVectoringJoystickModule.control(steerColumnAdapter, manualControlInterface);
    RimoPutEvent rimoPutEvent7 = control.get();
    System.out.println(rimoPutEvent7.putTireL.getTorque());
    System.out.println(rimoPutEvent7.putTireR.getTorque());
    assertTrue(Scalars.lessThan(rimoPutEvent7.putTireR.getTorque(), rimoPutEvent7.putTireL.getTorque().negate()));
    Scalar meanPower = rimoPutEvent7.putTireL.getTorque().negate().add(rimoPutEvent7.putTireR.getTorque()).divide(Quantity.of(2, SI.ONE));
    Scalar wantedPower = ManualConfig.GLOBAL.torqueLimit.multiply(slip);
    assertTrue(Scalars.lessThan(meanPower.subtract(wantedPower).abs(), Quantity.of(1, NonSI.ARMS)));
    torqueVectoringJoystickModule.last();
  }
}
