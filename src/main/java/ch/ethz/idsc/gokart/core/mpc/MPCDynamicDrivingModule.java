// code by mh, jph
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Optional;

import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.red.Max;

public class MPCDynamicDrivingModule extends MPCAbstractDrivingModule {
  public MPCDynamicDrivingModule() {
    super(LcmMPCControlClient.dynamic(), Timing.started());
  }

  // for testing only
  MPCDynamicDrivingModule(MPCStateEstimationProvider mpcStateEstimationProvider, Timing timing, MPCPreviewableTrack track) {
    super(LcmMPCControlClient.dynamic(), mpcStateEstimationProvider, timing, track);
  }

  @Override // from MPCAbstractDrivingModule
  MPCPower createPower(MPCStateEstimationProvider mpcStateEstimationProvider, MPCSteering mpcSteering) {
    return new MPCExplicitTorqueVectoringPower(mpcStateEstimationProvider);
  }

  @Override // from MPCAbstractDrivingModule
  MPCOptimizationParameter createOptimizationParameter() {
    Scalar maxSpeed = mpcOptimizationConfig.maxSpeed;
    Scalar minSpeed = mpcOptimizationConfig.minSpeed;
    Scalar mpcMaxSpeed = minSpeed;
    Optional<ManualControlInterface> optional = manualControlProvider.getManualControl();
    if (optional.isPresent()) { // is joystick button "autonomoRus" pressed?
      ManualControlInterface actualJoystick = optional.get();
      Scalar forward = actualJoystick.getAheadPair_Unit().Get(1);
      mpcMaxSpeed = maxSpeed.multiply(forward);
      mpcMaxSpeed = Max.of(minSpeed, mpcMaxSpeed);
      // maxSpeed = Quantity.of(1, SI.VELOCITY);
      // System.out.println("got joystick speed value: " + maxSpeed);
    }
    // send message with max speed
    // optimization parameters will have more values in the future
    // MPCOptimizationParameter mpcOptimizationParameter = new MPCOptimizationParameter(maxSpeed, maxXacc, maxYacc);
    return new MPCOptimizationParameterDynamic(mpcMaxSpeed);
  }
}
