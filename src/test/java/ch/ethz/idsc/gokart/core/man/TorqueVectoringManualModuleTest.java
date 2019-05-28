// code by mh, jph
package ch.ethz.idsc.gokart.core.man;

import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnAdapter;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class TorqueVectoringManualModuleTest extends TestCase {
  public void testSimple() throws Exception {
    TorqueVectoringModule torqueVectoringManualModule = new DirectTorqueVectoringModule();
    torqueVectoringManualModule.first();
    torqueVectoringManualModule.last();
  }

  public void testControl() throws Exception {
    TorqueVectoringModule torqueVectoringManualModule = new DirectTorqueVectoringModule();
    torqueVectoringManualModule.first();
    torqueVectoringManualModule.getEvent(RimoGetEvents.create(100, 200));
    SteerColumnAdapter steerColumnAdapter = new SteerColumnAdapter(true, Quantity.of(0, "SCE"));
    RimoPutEvent rimoPutEvent1 = torqueVectoringManualModule.derive( //
        steerColumnAdapter, RealScalar.ZERO, Quantity.of(0.0, SI.PER_SECOND));
    assertEquals(rimoPutEvent1.putTireL.getTorque(), Quantity.of(0, NonSI.ARMS));
    assertEquals(rimoPutEvent1.putTireR.getTorque(), Quantity.of(0, NonSI.ARMS));
    // full forward
    torqueVectoringManualModule.getEvent(RimoGetEvents.create(200, 200));
    steerColumnAdapter = new SteerColumnAdapter(true, Quantity.of(0, "SCE"));
    RimoPutEvent rimoPutEvent2 = torqueVectoringManualModule.derive( //
        steerColumnAdapter, RealScalar.ONE, Quantity.of(0.0, SI.PER_SECOND));
    assertEquals(rimoPutEvent2.putTireL.getTorque(), ManualConfig.GLOBAL.torqueLimit.negate());
    assertEquals(rimoPutEvent2.putTireR.getTorque(), ManualConfig.GLOBAL.torqueLimit);
    // half forward slip right
    torqueVectoringManualModule.last();
  }

  public void testControl2() throws Exception {
    TorqueVectoringModule torqueVectoringManualModule = new DirectTorqueVectoringModule();
    torqueVectoringManualModule.first();
    Scalar slip = RationalScalar.HALF; // 1/2 forward slip right
    torqueVectoringManualModule.getEvent(RimoGetEvents.create(200, 200));
    SteerColumnAdapter steerColumnAdapter = new SteerColumnAdapter(true, Quantity.of(0.1, "SCE"));
    RimoPutEvent rimoPutEvent3 = torqueVectoringManualModule.derive( //
        steerColumnAdapter, RealScalar.of(0.5), Quantity.of(-0.2, SI.PER_SECOND));
    Clips.interval(Quantity.of(-400, "ARMS"), Quantity.of(-300, "ARMS")).requireInside(rimoPutEvent3.putTireL.getTorque());
    Clips.interval(Quantity.of(1900, "ARMS"), Quantity.of(2000, "ARMS")).requireInside(rimoPutEvent3.putTireR.getTorque());
    assertTrue(Scalars.lessThan(rimoPutEvent3.putTireL.getTorque().negate(), rimoPutEvent3.putTireR.getTorque()));
    Sign.requirePositive(rimoPutEvent3.putTireR.getTorque());
    Scalar meanPower = rimoPutEvent3.putTireL.getTorque().negate().add(rimoPutEvent3.putTireR.getTorque()).divide(Quantity.of(2, SI.ONE));
    Scalar wantedPower = ManualConfig.GLOBAL.torqueLimit.multiply(slip);
    assertTrue(Scalars.lessThan(meanPower.subtract(wantedPower).abs(), Quantity.of(1, NonSI.ARMS)));
    torqueVectoringManualModule.last();
  }

  public void testControl3() throws Exception {
    TorqueVectoringModule torqueVectoringManualModule = new DirectTorqueVectoringModule();
    torqueVectoringManualModule.first();
    Scalar slip = RationalScalar.of(3, 4); // 3/4 forward slip right
    torqueVectoringManualModule.getEvent(RimoGetEvents.create(200, 200));
    SteerColumnAdapter steerColumnAdapter = new SteerColumnAdapter(true, Quantity.of(0.1, "SCE"));
    RimoPutEvent rimoPutEvent4 = torqueVectoringManualModule.derive( //
        steerColumnAdapter, RealScalar.of(0.75), Quantity.of(-0.3, SI.PER_SECOND));
    Clips.interval(Quantity.of(-1200, "ARMS"), Quantity.of(-1100, "ARMS")).requireInside(rimoPutEvent4.putTireL.getTorque());
    Clips.interval(Quantity.of(+2300, "ARMS"), Quantity.of(+2315, "ARMS")).requireInside(rimoPutEvent4.putTireR.getTorque());
    assertTrue(Scalars.lessThan(rimoPutEvent4.putTireL.getTorque().negate(), rimoPutEvent4.putTireR.getTorque()));
    assertTrue(Scalars.lessThan(Quantity.of(0, NonSI.ARMS), rimoPutEvent4.putTireR.getTorque()));
    Scalar meanPower = rimoPutEvent4.putTireL.getTorque().negate().add(rimoPutEvent4.putTireR.getTorque()).divide(Quantity.of(2, SI.ONE));
    Scalar wantedPower = ManualConfig.GLOBAL.torqueLimit.multiply(slip);
    assertTrue(Scalars.lessThan(meanPower.subtract(wantedPower).abs(), Quantity.of(1, NonSI.ARMS)));
    torqueVectoringManualModule.last();
  }

  public void testControl4() throws Exception {
    TorqueVectoringModule torqueVectoringManualModule = new DirectTorqueVectoringModule();
    torqueVectoringManualModule.first();
    Scalar slip = RationalScalar.of(3, 4); // 3/4 forward slip left
    // System.out.println(slip + " slip left");
    // DavisImuTracker.INSTANCE.setGyroZ();
    torqueVectoringManualModule.getEvent(RimoGetEvents.create(200, 200));
    SteerColumnAdapter steerColumnAdapter = new SteerColumnAdapter(true, Quantity.of(-0.1, "SCE"));
    RimoPutEvent rimoPutEvent5 = torqueVectoringManualModule.derive( //
        steerColumnAdapter, RealScalar.of(0.75), Quantity.of(0.3, SI.PER_SECOND));
    // System.out.println(rimoPutEvent5.putTireL.getTorque());
    // System.out.println(rimoPutEvent5.putTireR.getTorque());
    assertTrue(Scalars.lessThan(rimoPutEvent5.putTireR.getTorque(), rimoPutEvent5.putTireL.getTorque().negate()));
    assertTrue(Scalars.lessThan(Quantity.of(0, NonSI.ARMS), rimoPutEvent5.putTireL.getTorque().negate()));
    Scalar meanPower = rimoPutEvent5.putTireL.getTorque().negate().add(rimoPutEvent5.putTireR.getTorque()).divide(Quantity.of(2, SI.ONE));
    Scalar wantedPower = ManualConfig.GLOBAL.torqueLimit.multiply(slip);
    assertTrue(Scalars.lessThan(meanPower.subtract(wantedPower).abs(), Quantity.of(1, NonSI.ARMS)));
    torqueVectoringManualModule.last();
  }

  public void testControl5() throws Exception {
    TorqueVectoringModule torqueVectoringManualModule = new DirectTorqueVectoringModule();
    torqueVectoringManualModule.first();
    Scalar slip = RationalScalar.of(-3, 4); // 3/4 forward slip right
    // System.out.println(slip + " slip right");
    // DavisImuTracker.INSTANCE.setGyroZ();
    torqueVectoringManualModule.getEvent(RimoGetEvents.create(200, 200));
    SteerColumnAdapter steerColumnAdapter = new SteerColumnAdapter(true, Quantity.of(0.1, "SCE"));
    RimoPutEvent rimoPutEvent6 = torqueVectoringManualModule.derive( //
        steerColumnAdapter, RealScalar.of(-0.75), Quantity.of(-0.3, SI.PER_SECOND));
    // System.out.println(rimoPutEvent6.putTireL.getTorque());
    // System.out.println(rimoPutEvent6.putTireR.getTorque());
    assertTrue(Scalars.lessThan(rimoPutEvent6.putTireL.getTorque().negate(), rimoPutEvent6.putTireR.getTorque()));
    Scalar meanPower = rimoPutEvent6.putTireL.getTorque().negate().add(rimoPutEvent6.putTireR.getTorque()).divide(Quantity.of(2, SI.ONE));
    Scalar wantedPower = ManualConfig.GLOBAL.torqueLimit.multiply(slip);
    assertTrue(Scalars.lessThan(meanPower.subtract(wantedPower).abs(), Quantity.of(1, NonSI.ARMS)));
    torqueVectoringManualModule.last();
  }

  public void testControl6() throws Exception {
    TorqueVectoringModule torqueVectoringManualModule = new DirectTorqueVectoringModule();
    torqueVectoringManualModule.first();
    Scalar slip = RationalScalar.of(-3, 4); // 3/4 forward slip left
    // System.out.println(slip + " slip left");
    // DavisImuTracker.INSTANCE.setGyroZ();
    torqueVectoringManualModule.getEvent(RimoGetEvents.create(200, 200));
    SteerColumnAdapter steerColumnAdapter = new SteerColumnAdapter(true, Quantity.of(-0.1, "SCE"));
    RimoPutEvent rimoPutEvent7 = torqueVectoringManualModule.derive( //
        steerColumnAdapter, RealScalar.of(-0.75), Quantity.of(0.3, SI.PER_SECOND));
    // System.out.println(rimoPutEvent7.putTireL.getTorque());
    // System.out.println(rimoPutEvent7.putTireR.getTorque());
    assertTrue(Scalars.lessThan(rimoPutEvent7.putTireR.getTorque(), rimoPutEvent7.putTireL.getTorque().negate()));
    Scalar meanPower = rimoPutEvent7.putTireL.getTorque().negate().add(rimoPutEvent7.putTireR.getTorque()).divide(Quantity.of(2, SI.ONE));
    Scalar wantedPower = ManualConfig.GLOBAL.torqueLimit.multiply(slip);
    assertTrue(Scalars.lessThan(meanPower.subtract(wantedPower).abs(), Quantity.of(1, NonSI.ARMS)));
    torqueVectoringManualModule.last();
  }
}
