// code by mh, jph
package ch.ethz.idsc.gokart.core.man;

import ch.ethz.idsc.gokart.calib.steer.RimoTwdOdometry;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnAdapter;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class TorqueVectoringModuleTest extends TestCase {
  public void testControl2() throws Exception {
    TorqueVectoringModule torqueVectoringModule = new DirectTorqueVectoringModule();
    torqueVectoringModule.first();
    Scalar slip = RationalScalar.HALF; // 1/2 forward slip right
    RimoGetEvent rimoGetEvent = RimoGetEvents.create(200, 200);
    Scalar vx = RimoTwdOdometry.tangentSpeed(rimoGetEvent);
    SteerColumnAdapter steerColumnAdapter = new SteerColumnAdapter(true, Quantity.of(0.1, "SCE"));
    RimoPutEvent rimoPutEvent = torqueVectoringModule.derive( //
        steerColumnAdapter, RealScalar.of(0.5), Tensors.of(vx, vx.zero(), Quantity.of(-0.2, SI.PER_SECOND)));
    Clips.interval(Quantity.of(-400, "ARMS"), Quantity.of(-300, "ARMS")).requireInside(rimoPutEvent.putTireL.getTorque());
    Clips.interval(Quantity.of(1900, "ARMS"), Quantity.of(2000, "ARMS")).requireInside(rimoPutEvent.putTireR.getTorque());
    assertTrue(Scalars.lessThan(rimoPutEvent.putTireL.getTorque().negate(), rimoPutEvent.putTireR.getTorque()));
    Sign.requirePositive(rimoPutEvent.putTireR.getTorque());
    Scalar meanPower = rimoPutEvent.putTireL.getTorque().negate().add(rimoPutEvent.putTireR.getTorque()).divide(Quantity.of(2, SI.ONE));
    Scalar wantedPower = ManualConfig.GLOBAL.torqueLimit.multiply(slip);
    assertTrue(Scalars.lessThan(meanPower.subtract(wantedPower).abs(), Quantity.of(1, NonSI.ARMS)));
    torqueVectoringModule.last();
  }

  public void testControl3() throws Exception {
    TorqueVectoringModule torqueVectoringModule = new DirectTorqueVectoringModule();
    torqueVectoringModule.first();
    Scalar slip = RationalScalar.of(3, 4); // 3/4 forward slip right
    RimoGetEvent rimoGetEvent = RimoGetEvents.create(200, 200);
    Scalar vx = RimoTwdOdometry.tangentSpeed(rimoGetEvent);
    SteerColumnAdapter steerColumnAdapter = new SteerColumnAdapter(true, Quantity.of(0.1, "SCE"));
    RimoPutEvent rimoPutEvent = torqueVectoringModule.derive( //
        steerColumnAdapter, RealScalar.of(0.75), Tensors.of(vx, vx.zero(), Quantity.of(-0.3, SI.PER_SECOND)));
    Clips.interval(Quantity.of(-1200, "ARMS"), Quantity.of(-1100, "ARMS")).requireInside(rimoPutEvent.putTireL.getTorque());
    Clips.interval(Quantity.of(+2300, "ARMS"), Quantity.of(+2315, "ARMS")).requireInside(rimoPutEvent.putTireR.getTorque());
    assertTrue(Scalars.lessThan(rimoPutEvent.putTireL.getTorque().negate(), rimoPutEvent.putTireR.getTorque()));
    assertTrue(Scalars.lessThan(Quantity.of(0, NonSI.ARMS), rimoPutEvent.putTireR.getTorque()));
    Scalar meanPower = rimoPutEvent.putTireL.getTorque().negate().add(rimoPutEvent.putTireR.getTorque()).divide(Quantity.of(2, SI.ONE));
    Scalar wantedPower = ManualConfig.GLOBAL.torqueLimit.multiply(slip);
    assertTrue(Scalars.lessThan(meanPower.subtract(wantedPower).abs(), Quantity.of(1, NonSI.ARMS)));
    torqueVectoringModule.last();
  }

  public void testControl4() throws Exception {
    TorqueVectoringModule torqueVectoringModule = new DirectTorqueVectoringModule();
    torqueVectoringModule.first();
    Scalar slip = RationalScalar.of(3, 4); // 3/4 forward slip left
    // System.out.println(slip + " slip left");
    // DavisImuTracker.INSTANCE.setGyroZ();
    // torqueVectoringModule.getEvent();
    RimoGetEvent rimoGetEvent = RimoGetEvents.create(200, 200);
    Scalar vx = RimoTwdOdometry.tangentSpeed(rimoGetEvent);
    SteerColumnAdapter steerColumnAdapter = new SteerColumnAdapter(true, Quantity.of(-0.1, "SCE"));
    RimoPutEvent rimoPutEvent = torqueVectoringModule.derive( //
        steerColumnAdapter, RealScalar.of(0.75), Tensors.of(vx, vx.zero(), Quantity.of(0.3, SI.PER_SECOND)));
    // System.out.println(rimoPutEvent5.putTireL.getTorque());
    // System.out.println(rimoPutEvent5.putTireR.getTorque());
    assertTrue(Scalars.lessThan(rimoPutEvent.putTireR.getTorque(), rimoPutEvent.putTireL.getTorque().negate()));
    assertTrue(Scalars.lessThan(Quantity.of(0, NonSI.ARMS), rimoPutEvent.putTireL.getTorque().negate()));
    Scalar meanPower = rimoPutEvent.putTireL.getTorque().negate().add(rimoPutEvent.putTireR.getTorque()).divide(Quantity.of(2, SI.ONE));
    Scalar wantedPower = ManualConfig.GLOBAL.torqueLimit.multiply(slip);
    assertTrue(Scalars.lessThan(meanPower.subtract(wantedPower).abs(), Quantity.of(1, NonSI.ARMS)));
    torqueVectoringModule.last();
  }

  public void testControl5() throws Exception {
    TorqueVectoringModule torqueVectoringModule = new DirectTorqueVectoringModule();
    torqueVectoringModule.first();
    Scalar slip = RationalScalar.of(-3, 4); // 3/4 forward slip right
    // System.out.println(slip + " slip right");
    // DavisImuTracker.INSTANCE.setGyroZ();
    RimoGetEvent rimoGetEvent = RimoGetEvents.create(200, 200);
    Scalar vx = RimoTwdOdometry.tangentSpeed(rimoGetEvent);
    SteerColumnAdapter steerColumnAdapter = new SteerColumnAdapter(true, Quantity.of(0.1, "SCE"));
    RimoPutEvent rimoPutEvent = torqueVectoringModule.derive( //
        steerColumnAdapter, RealScalar.of(-0.75), Tensors.of(vx, vx.zero(), Quantity.of(-0.3, SI.PER_SECOND)));
    // System.out.println(rimoPutEvent6.putTireL.getTorque());
    // System.out.println(rimoPutEvent6.putTireR.getTorque());
    assertTrue(Scalars.lessThan(rimoPutEvent.putTireL.getTorque().negate(), rimoPutEvent.putTireR.getTorque()));
    Scalar meanPower = rimoPutEvent.putTireL.getTorque().negate().add(rimoPutEvent.putTireR.getTorque()).divide(Quantity.of(2, SI.ONE));
    Scalar wantedPower = ManualConfig.GLOBAL.torqueLimit.multiply(slip);
    assertTrue(Scalars.lessThan(meanPower.subtract(wantedPower).abs(), Quantity.of(1, NonSI.ARMS)));
    torqueVectoringModule.last();
  }

  public void testControl6() throws Exception {
    TorqueVectoringModule torqueVectoringModule = new DirectTorqueVectoringModule();
    torqueVectoringModule.first();
    Scalar slip = RationalScalar.of(-3, 4); // 3/4 forward slip left
    // System.out.println(slip + " slip left");
    // DavisImuTracker.INSTANCE.setGyroZ();
    RimoGetEvent rimoGetEvent = RimoGetEvents.create(200, 200);
    Scalar vx = RimoTwdOdometry.tangentSpeed(rimoGetEvent);
    SteerColumnAdapter steerColumnAdapter = new SteerColumnAdapter(true, Quantity.of(-0.1, "SCE"));
    RimoPutEvent rimoPutEvent7 = torqueVectoringModule.derive( //
        steerColumnAdapter, RealScalar.of(-0.75), Tensors.of(vx, vx.zero(), Quantity.of(0.3, SI.PER_SECOND)));
    // System.out.println(rimoPutEvent7.putTireL.getTorque());
    // System.out.println(rimoPutEvent7.putTireR.getTorque());
    assertTrue(Scalars.lessThan(rimoPutEvent7.putTireR.getTorque(), rimoPutEvent7.putTireL.getTorque().negate()));
    Scalar meanPower = rimoPutEvent7.putTireL.getTorque().negate().add(rimoPutEvent7.putTireR.getTorque()).divide(Quantity.of(2, SI.ONE));
    Scalar wantedPower = ManualConfig.GLOBAL.torqueLimit.multiply(slip);
    assertTrue(Scalars.lessThan(meanPower.subtract(wantedPower).abs(), Quantity.of(1, NonSI.ARMS)));
    torqueVectoringModule.last();
  }
}
