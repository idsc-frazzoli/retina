// code by am
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.DubendorfCurve;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnAdapter;
import ch.ethz.idsc.gokart.dev.steer.SteerGetEvents;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class LaneKeepingLimitedSteeringModuleTest extends TestCase {
  public void testSimple() {
    LaneKeepingLimitedSteeringModule laneKeepingLimitedSteeringModule = new LaneKeepingLimitedSteeringModule();
    laneKeepingLimitedSteeringModule.launch();
    assertFalse(laneKeepingLimitedSteeringModule.putEvent().isPresent());
    laneKeepingLimitedSteeringModule.terminate();
  }

  private static final Tensor CURVE = DubendorfCurve.TRACK_OVAL_SE2;

  public void testSimple1() {
    LaneKeepingLimitedSteeringModule laneKeepingLimitedSteeringModule = new LaneKeepingLimitedSteeringModule();
    laneKeepingLimitedSteeringModule.launch();
    Tensor pose = CURVE.get(3);
    assertFalse(laneKeepingLimitedSteeringModule.getCurve().isPresent());
    laneKeepingLimitedSteeringModule.setCurve(Optional.of(CURVE));
    assertTrue(laneKeepingLimitedSteeringModule.getCurve().isPresent());
    Optional<Clip> permittedRange = laneKeepingLimitedSteeringModule.getPermittedRange(CURVE, pose);
    assertTrue(permittedRange.isPresent());
    Clip clip = permittedRange.get();
    Scalar width = clip.width();
    assertTrue(Scalars.lessThan(Quantity.of(0.2, "SCE"), width));
    assertTrue(Scalars.lessThan(width, Quantity.of(0.7, "SCE")));
    System.out.println(clip);
    laneKeepingLimitedSteeringModule.runAlgo();
    {
      PowerSteering powerSteering = new PowerSteering(HapticSteerConfig.GLOBAL);
      Scalar currangle = Quantity.of(0.1, "SCE");
      Scalar powerSteeringTorque = powerSteering.torque(currangle, GokartPoseEvents.motionlessUninitialized().getVelocity(), Quantity.of(0, "SCT"));
      Optional<SteerPutEvent> optional = laneKeepingLimitedSteeringModule.putEvent( //
          new SteerColumnAdapter(true, currangle), //
          SteerGetEvents.ZEROS, permittedRange);
      assertTrue(optional.isPresent());
      SteerPutEvent steerPutEvent = optional.get();
      // Chop._05.requireClose(powerSteeringTorque, steerPutEvent.getTorque());
    }
    {
      PowerSteering powerSteering = new PowerSteering(HapticSteerConfig.GLOBAL);
      Scalar currangle = Quantity.of(0.4, "SCE");
      Scalar powerSteeringTorque = powerSteering.torque(currangle, GokartPoseEvents.motionlessUninitialized().getVelocity(), Quantity.of(0, "SCT"));
      System.out.println("power steer=" + powerSteeringTorque);
      Optional<SteerPutEvent> optional = laneKeepingLimitedSteeringModule.putEvent( //
          new SteerColumnAdapter(true, currangle), //
          SteerGetEvents.ZEROS, permittedRange);
      assertTrue(optional.isPresent());
      SteerPutEvent steerPutEvent = optional.get();
      assertTrue(Scalars.lessThan(steerPutEvent.getTorque(), powerSteeringTorque));
    }
    {
      PowerSteering powerSteering = new PowerSteering(HapticSteerConfig.GLOBAL);
      Scalar currangle = Quantity.of(-0.4, "SCE");
      Scalar powerSteeringTorque = powerSteering.torque(currangle, GokartPoseEvents.motionlessUninitialized().getVelocity(), Quantity.of(0, "SCT"));
      System.out.println("power steer=" + powerSteeringTorque);
      Optional<SteerPutEvent> optional = laneKeepingLimitedSteeringModule.putEvent( //
          new SteerColumnAdapter(true, currangle), //
          SteerGetEvents.ZEROS, permittedRange);
      assertTrue(optional.isPresent());
      SteerPutEvent steerPutEvent = optional.get();
      assertTrue(Scalars.lessThan(powerSteeringTorque, steerPutEvent.getTorque()));
    }
    laneKeepingLimitedSteeringModule.terminate();
  }
}
