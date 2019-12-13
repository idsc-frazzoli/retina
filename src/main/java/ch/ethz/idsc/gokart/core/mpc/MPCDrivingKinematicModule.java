// code by mh, jph
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Optional;

import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.red.Max;

public class MPCDrivingKinematicModule extends MPCDrivingAbstractModule {
  public MPCDrivingKinematicModule() {
    super(MPCRequestPublisher.kinematic(), Timing.started());
  }

  // for testing only
  MPCDrivingKinematicModule(MPCStateEstimationProvider mpcStateEstimationProvider, Timing timing, MPCPreviewableTrack track) {
    super(MPCRequestPublisher.kinematic(), mpcStateEstimationProvider, timing, track);
  }

  @Override // from MPCAbstractDrivingModule
  MPCPower createPower(MPCStateEstimationProvider mpcStateEstimationProvider, MPCSteering mpcSteering) {
    return new MPCAggressiveTorqueVectoringPower(mpcStateEstimationProvider, mpcSteering);
  }

  @Override // from MPCAbstractDrivingModule
  MPCOptimizationParameterKinematic createOptimizationParameter( //
      MPCOptimizationConfig mpcOptimizationConfig, Optional<ManualControlInterface> optional) {
    return optimizationParameter(mpcOptimizationConfig, optional);
  }

  // package for testing
  static MPCOptimizationParameterKinematic optimizationParameter( //
      MPCOptimizationConfig mpcOptimizationConfig, Optional<ManualControlInterface> optional) {
    final Scalar minSpeed = mpcOptimizationConfig.minSpeed;
    final Scalar mpcMaxSpeed;
    if (optional.isPresent()) {
      ManualControlInterface manualControlInterface = optional.get();
      Scalar forward = manualControlInterface.getAheadPair_Unit().Get(1);
      mpcMaxSpeed = Max.of(minSpeed, mpcOptimizationConfig.maxSpeed.multiply(forward));
    } else
      mpcMaxSpeed = minSpeed; // fallback speed value
    return new MPCOptimizationParameterKinematic( //
        mpcMaxSpeed, //
        mpcOptimizationConfig.maxLonAcc, //
        mpcOptimizationConfig.maxLatAcc, //
        mpcOptimizationConfig.latAccLim, //
        mpcOptimizationConfig.rotAccEffect, //
        mpcOptimizationConfig.torqueVecEffect, //
        mpcOptimizationConfig.brakeEffect);
  }

  @Override // from MPCDrivingAbstractModule
  protected final boolean torqueBased() {
    return false;
  }
  
  protected final boolean PowerSteeringUsed() {
    return false;
  }
}
