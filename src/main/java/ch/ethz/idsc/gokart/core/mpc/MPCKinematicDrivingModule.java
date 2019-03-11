// code by mh, jph
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Optional;

import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.red.Max;

public class MPCKinematicDrivingModule extends MPCAbstractDrivingModule {
  public MPCKinematicDrivingModule() {
    super(LcmMPCControlClient.kinematic(), Timing.started());
  }

  // for testing only
  MPCKinematicDrivingModule(MPCStateEstimationProvider mpcStateEstimationProvider, Timing timing, MPCPreviewableTrack track) {
    super(LcmMPCControlClient.kinematic(), mpcStateEstimationProvider, timing, track);
  }

  @Override // from MPCAbstractDrivingModule
  MPCPower createPower(MPCStateEstimationProvider mpcStateEstimationProvider, MPCSteering mpcSteering) {
    return new MPCAggressiveTorqueVectoringPower(mpcStateEstimationProvider, mpcSteering);
  }

  @Override // from MPCAbstractDrivingModule
  MPCOptimizationParameter createOptimizationParameter() {
    Scalar maxSpeed = mpcOptimizationConfig.maxSpeed;
    Scalar minSpeed = mpcOptimizationConfig.minSpeed;
    Scalar maxXacc = mpcOptimizationConfig.maxLonAcc;
    Scalar maxYacc = mpcOptimizationConfig.maxLatAcc;
    Scalar latAccLim = mpcOptimizationConfig.latAccLim;
    Scalar rotAccEffect = mpcOptimizationConfig.rotAccEffect;
    Scalar torqueVecEffect = mpcOptimizationConfig.torqueVecEffect;
    Scalar brakeEffect = mpcOptimizationConfig.brakeEffect;
    Scalar mpcMaxSpeed = minSpeed;
    Optional<ManualControlInterface> optionalJoystick = manualControlProvider.getManualControl();
    if (optionalJoystick.isPresent()) { // is joystick button "autonomoRus" pressed?
      ManualControlInterface actualJoystick = optionalJoystick.get();
      Scalar forward = actualJoystick.getAheadPair_Unit().Get(1);
      mpcMaxSpeed = maxSpeed.multiply(forward);
      mpcMaxSpeed = Max.of(minSpeed, mpcMaxSpeed);
      // maxSpeed = Quantity.of(1, SI.VELOCITY);
      // System.out.println("got joystick speed value: " + maxSpeed);
    }
    // send message with max speed
    // optimization parameters will have more values in the future
    // MPCOptimizationParameter mpcOptimizationParameter = new MPCOptimizationParameter(maxSpeed, maxXacc, maxYacc);
    return new MPCOptimizationParameterKinematic( //
        mpcMaxSpeed, maxXacc, maxYacc, latAccLim, rotAccEffect, torqueVecEffect, brakeEffect);
  }
}
