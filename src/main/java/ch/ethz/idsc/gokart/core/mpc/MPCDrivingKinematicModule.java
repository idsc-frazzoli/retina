// code by mh, jph
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Optional;

import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.Timing;

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
    final Scalar mpcMaxSpeed = //
        optional.map(MPCDrivingAbstractModule.toMPCmaxSpeed(minSpeed, mpcOptimizationConfig.maxSpeed)).orElse(minSpeed);
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

  @Override // from MPCDrivingAbstractModule
  protected final boolean powerSteeringUsed() {
    return false;
  }
}
