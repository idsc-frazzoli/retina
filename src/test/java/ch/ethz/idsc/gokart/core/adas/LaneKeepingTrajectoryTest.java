// code by am
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pure.DubendorfCurve;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class LaneKeepingTrajectoryTest extends TestCase {
  private static final Tensor CURVE = DubendorfCurve.TRACK_OVAL_SE2;

  public void testSimple() {
    LaneKeepingCenterlineModule laneKeepingCenterlineModule = new LaneKeepingCenterlineModule();
    laneKeepingCenterlineModule.first();
    laneKeepingCenterlineModule.setCurve(Optional.ofNullable(CURVE));
    assertTrue(laneKeepingCenterlineModule.getCurve().isPresent());
    laneKeepingCenterlineModule.last();
  }

  public void testSimple4() {
    // was sind die Einheiten von Curve
    LeftLaneModule leftLaneModule = new LeftLaneModule();
    Optional<Tensor> curve = Optional.of(Tensors.fromString("{{1[m], 1[m], 2}, {3[m], 2[m], 4}}"));
    Tensor pose = Tensors.of(//
        Quantity.of(10000, SI.METER), //
        Quantity.of(10000, SI.METER), //
        RealScalar.of(0));
    GokartPoseEvent testEvent = GokartPoseEvents.create(pose, RealScalar.ONE);
    Scalar criticalDistance = Quantity.of(1, SI.METER);
    leftLaneModule.leftLane(curve, testEvent, criticalDistance);
    System.out.println(" ");
  }

  public void testSimple2() {
    MeasurementSlowDownModule slowDown = new MeasurementSlowDownModule();
    slowDown.first();
    RimoGetEvent rimoGetEvent = RimoGetEvents.create(5000, 5000);
    slowDown.rimoGetListener.getEvent(rimoGetEvent);
    RimoPutEvent rimoPutEvent = slowDown.putEvent().get();
    System.out.println(rimoPutEvent.getTorque_Y_pair());
    slowDown.last();
    System.out.println(" ");
  }

  public void testSimple3() {
    LaneKeepingSlowDownModule laneKeepingSlowDownModule = new LaneKeepingSlowDownModule();
    laneKeepingSlowDownModule.first();
    laneKeepingSlowDownModule.putEvent();
    laneKeepingSlowDownModule.last();
    System.out.println(" ");
  }

  public void testSimple5() {
    LaneKeepingLimitedSteeringModule laneKeepingLimitedSteeringModule = new LaneKeepingLimitedSteeringModule();
    laneKeepingLimitedSteeringModule.first();
    Optional<Tensor> curve = Optional.of(Tensors.fromString("{{1[m], 1[m], 2}, {3[m], 2[m], 4}}"));
    Tensor pose = Tensors.of(//
        Quantity.of(10000, SI.METER), //
        Quantity.of(10000, SI.METER), //
        RealScalar.of(0));
    GokartPoseEvent testEvent = GokartPoseEvents.create(pose, RealScalar.ONE);
    laneKeepingLimitedSteeringModule.getPlan(curve, testEvent);
    laneKeepingLimitedSteeringModule.last();
    System.out.println(" ");
  }

  public void testSimple6() {
    LaneKeepingLimitedSteeringModule laneKeepingLimitedSteeringModule = new LaneKeepingLimitedSteeringModule();
    laneKeepingLimitedSteeringModule.first();
    laneKeepingLimitedSteeringModule.putEvent();
    laneKeepingLimitedSteeringModule.last();
    System.out.println(" ");
  }
}
