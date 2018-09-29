// code by mh, jph
package ch.ethz.idsc.gokart.core.joy;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerColumnAdapter;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class TorqueVectoringJoystickModuleTest extends TestCase {
  public void testSimple() throws Exception {
    TorqueVectoringJoystickModule tvjm = new TorqueVectoringJoystickModule();
    tvjm.first();
    tvjm.last();
  }

  public void testControl() throws Exception {
    TorqueVectoringJoystickModule tvjm = new TorqueVectoringJoystickModule();
    tvjm.first();
    tvjm.gyro_Z = Quantity.of(0, SI.PER_SECOND);
    tvjm.getEvent(RimoGetEvents.create(100, 200));
    SteerColumnAdapter steerColumnAdapter = new SteerColumnAdapter(true, Quantity.of(0, "SCE"));
    GokartJoystickInterface joystick = new GokartJoystickAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(0), Tensors.vector(0, 0), false);
    Optional<RimoPutEvent> control = tvjm.control(steerColumnAdapter, joystick);
    RimoPutEvent rimoPutEvent1 = control.get();
    System.out.println(rimoPutEvent1.putTireL.getTorque());
    System.out.println(rimoPutEvent1.putTireR.getTorque());
    assertEquals(rimoPutEvent1.putTireL.getTorque(), Quantity.of(0, NonSI.ARMS));
    assertEquals(rimoPutEvent1.putTireR.getTorque(), Quantity.of(0, NonSI.ARMS));
    // full forward
    System.out.println("full forward");
    tvjm.gyro_Z = Quantity.of(0, SI.PER_SECOND);
    tvjm.getEvent(RimoGetEvents.create(200, 200));
    steerColumnAdapter = new SteerColumnAdapter(true, Quantity.of(0, "SCE"));
    joystick = new GokartJoystickAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(0), Tensors.vector(0, 1), false);
    control = tvjm.control(steerColumnAdapter, joystick);
    RimoPutEvent rimoPutEvent2 = control.get();
    System.out.println(rimoPutEvent2.putTireL.getTorque());
    System.out.println(rimoPutEvent2.putTireR.getTorque());
    // JoystickConfig.GLOBAL;
    // assertEquals(rimoPutEvent2.putTireL.getTorque(), JoystickConfig.GLOBAL.torqueLimit);
    // assertEquals(rimoPutEvent2.putTireR.getTorque(), JoystickConfig.GLOBAL.torqueLimit);
    assertEquals(rimoPutEvent2.putTireL.getTorque(), JoystickConfig.GLOBAL.torqueLimit.negate());
    assertEquals(rimoPutEvent2.putTireR.getTorque(), JoystickConfig.GLOBAL.torqueLimit);
    // half forward slip right
    tvjm.last();
  }

  public void testControl2() throws Exception {
    TorqueVectoringJoystickModule tvjm = new TorqueVectoringJoystickModule();
    tvjm.first();
    Scalar slip = RationalScalar.of(1, 2); // 1/2 forward slip right
    System.out.println(slip + " forward/slip right");
    tvjm.gyro_Z = Quantity.of(-0.2, SI.PER_SECOND);
    tvjm.getEvent(RimoGetEvents.create(200, 200));
    SteerColumnAdapter steerColumnAdapter = new SteerColumnAdapter(true, Quantity.of(0.1, "SCE"));
    GokartJoystickInterface joystick = new GokartJoystickAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(0), Tensors.vector(0, 0.5), false);
    Optional<RimoPutEvent> control = tvjm.control(steerColumnAdapter, joystick);
    RimoPutEvent rimoPutEvent3 = control.get();
    System.out.println(rimoPutEvent3.putTireL.getTorque());
    System.out.println(rimoPutEvent3.putTireR.getTorque());
    assertTrue(Scalars.lessThan(rimoPutEvent3.putTireL.getTorque().negate(), rimoPutEvent3.putTireR.getTorque()));
    assertTrue(Scalars.lessThan(Quantity.of(0, NonSI.ARMS), rimoPutEvent3.putTireR.getTorque()));
    Scalar meanPower = rimoPutEvent3.putTireL.getTorque().negate().add(rimoPutEvent3.putTireR.getTorque()).divide(Quantity.of(2, SI.ONE));
    Scalar wantedPower = JoystickConfig.GLOBAL.torqueLimit.multiply(slip);
    assertTrue(Scalars.lessThan(meanPower.subtract(wantedPower).abs(), Quantity.of(1, NonSI.ARMS)));
    tvjm.last();
  }

  public void testControl3() throws Exception {
    TorqueVectoringJoystickModule tvjm = new TorqueVectoringJoystickModule();
    tvjm.first();
    Scalar slip = RationalScalar.of(3, 4); // 3/4 forward slip right
    System.out.println(slip + " slip right");
    tvjm.gyro_Z = Quantity.of(-0.3, SI.PER_SECOND);
    tvjm.getEvent(RimoGetEvents.create(200, 200));
    SteerColumnAdapter steerColumnAdapter = new SteerColumnAdapter(true, Quantity.of(0.1, "SCE"));
    GokartJoystickInterface joystick = new GokartJoystickAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(0), Tensors.vector(0, 0.75), false);
    Optional<RimoPutEvent> control = tvjm.control(steerColumnAdapter, joystick);
    RimoPutEvent rimoPutEvent4 = control.get();
    System.out.println(rimoPutEvent4.putTireL.getTorque());
    System.out.println(rimoPutEvent4.putTireR.getTorque());
    assertTrue(Scalars.lessThan(rimoPutEvent4.putTireL.getTorque().negate(), rimoPutEvent4.putTireR.getTorque()));
    assertTrue(Scalars.lessThan(Quantity.of(0, NonSI.ARMS), rimoPutEvent4.putTireR.getTorque()));
    Scalar meanPower = rimoPutEvent4.putTireL.getTorque().negate().add(rimoPutEvent4.putTireR.getTorque()).divide(Quantity.of(2, SI.ONE));
    Scalar wantedPower = JoystickConfig.GLOBAL.torqueLimit.multiply(slip);
    assertTrue(Scalars.lessThan(meanPower.subtract(wantedPower).abs(), Quantity.of(1, NonSI.ARMS)));
    tvjm.last();
  }

  public void testControl4() throws Exception {
    TorqueVectoringJoystickModule tvjm = new TorqueVectoringJoystickModule();
    tvjm.first();
    Scalar slip = RationalScalar.of(3, 4); // 3/4 forward slip left
    System.out.println(slip + " slip left");
    tvjm.gyro_Z = Quantity.of(0.3, SI.PER_SECOND);
    tvjm.getEvent(RimoGetEvents.create(200, 200));
    SteerColumnAdapter steerColumnAdapter = new SteerColumnAdapter(true, Quantity.of(-0.1, "SCE"));
    GokartJoystickInterface joystick = new GokartJoystickAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(0), Tensors.vector(0, 0.75), false);
    Optional<RimoPutEvent> control = tvjm.control(steerColumnAdapter, joystick);
    RimoPutEvent rimoPutEvent5 = control.get();
    System.out.println(rimoPutEvent5.putTireL.getTorque());
    System.out.println(rimoPutEvent5.putTireR.getTorque());
    assertTrue(Scalars.lessThan(rimoPutEvent5.putTireR.getTorque(), rimoPutEvent5.putTireL.getTorque().negate()));
    assertTrue(Scalars.lessThan(Quantity.of(0, NonSI.ARMS), rimoPutEvent5.putTireL.getTorque().negate()));
    Scalar meanPower = rimoPutEvent5.putTireL.getTorque().negate().add(rimoPutEvent5.putTireR.getTorque()).divide(Quantity.of(2, SI.ONE));
    Scalar wantedPower = JoystickConfig.GLOBAL.torqueLimit.multiply(slip);
    assertTrue(Scalars.lessThan(meanPower.subtract(wantedPower).abs(), Quantity.of(1, NonSI.ARMS)));
    tvjm.last();
  }

  public void testControl5() throws Exception {
    TorqueVectoringJoystickModule tvjm = new TorqueVectoringJoystickModule();
    tvjm.first();
    Scalar slip = RationalScalar.of(-3, 4); // 3/4 forward slip right
    System.out.println(slip + " slip right");
    tvjm.gyro_Z = Quantity.of(-0.3, SI.PER_SECOND);
    tvjm.getEvent(RimoGetEvents.create(200, 200));
    SteerColumnAdapter steerColumnAdapter = new SteerColumnAdapter(true, Quantity.of(0.1, "SCE"));
    GokartJoystickInterface joystick = new GokartJoystickAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(0), Tensors.vector(0.75, 0), false);
    Optional<RimoPutEvent> control = tvjm.control(steerColumnAdapter, joystick);
    RimoPutEvent rimoPutEvent6 = control.get();
    System.out.println(rimoPutEvent6.putTireL.getTorque());
    System.out.println(rimoPutEvent6.putTireR.getTorque());
    assertTrue(Scalars.lessThan(rimoPutEvent6.putTireL.getTorque().negate(), rimoPutEvent6.putTireR.getTorque()));
    Scalar meanPower = rimoPutEvent6.putTireL.getTorque().negate().add(rimoPutEvent6.putTireR.getTorque()).divide(Quantity.of(2, SI.ONE));
    Scalar wantedPower = JoystickConfig.GLOBAL.torqueLimit.multiply(slip);
    assertTrue(Scalars.lessThan(meanPower.subtract(wantedPower).abs(), Quantity.of(1, NonSI.ARMS)));
    tvjm.last();
  }

  public void testControl6() throws Exception {
    TorqueVectoringJoystickModule tvjm = new TorqueVectoringJoystickModule();
    tvjm.first();
    Scalar slip = RationalScalar.of(-3, 4); // 3/4 forward slip left
    System.out.println(slip + " slip left");
    tvjm.gyro_Z = Quantity.of(0.3, SI.PER_SECOND);
    tvjm.getEvent(RimoGetEvents.create(200, 200));
    SteerColumnAdapter steerColumnAdapter = new SteerColumnAdapter(true, Quantity.of(-0.1, "SCE"));
    GokartJoystickInterface joystick = new GokartJoystickAdapter( //
        RealScalar.of(.1), RealScalar.ZERO, RealScalar.of(0), Tensors.vector(0.75, 0), false);
    Optional<RimoPutEvent> control = tvjm.control(steerColumnAdapter, joystick);
    RimoPutEvent rimoPutEvent7 = control.get();
    System.out.println(rimoPutEvent7.putTireL.getTorque());
    System.out.println(rimoPutEvent7.putTireR.getTorque());
    assertTrue(Scalars.lessThan(rimoPutEvent7.putTireR.getTorque(), rimoPutEvent7.putTireL.getTorque().negate()));
    Scalar meanPower = rimoPutEvent7.putTireL.getTorque().negate().add(rimoPutEvent7.putTireR.getTorque()).divide(Quantity.of(2, SI.ONE));
    Scalar wantedPower = JoystickConfig.GLOBAL.torqueLimit.multiply(slip);
    assertTrue(Scalars.lessThan(meanPower.subtract(wantedPower).abs(), Quantity.of(1, NonSI.ARMS)));
    tvjm.last();
  }
}
