// code by ta
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Optional;

import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.Timing;

public class MPCDrivingCombinedTorqueModule extends MPCDrivingCommonModule {
  public MPCDrivingCombinedTorqueModule() {
    super(MPCRequestPublisher.torque(), Timing.started());
  }

  // for testing only
  MPCDrivingCombinedTorqueModule(MPCStateEstimationProvider mpcStateEstimationProvider, Timing timing, MPCPreviewableTrack track) {
    super(MPCRequestPublisher.torque(), mpcStateEstimationProvider, timing, track);
  }

  @Override // from MPCAbstractDrivingModule
  MPCOptimizationParameterLudic createOptimizationParameter( //
      MPCOptimizationConfig mpcOptimizationConfig, Optional<ManualControlInterface> optional) {
    return optimizationParameter(mpcOptimizationConfig, optional);
  }

  private static MPCOptimizationParameterLudic optimizationParameter( //
      MPCOptimizationConfig mpcOptimizationConfig, Optional<ManualControlInterface> optional) {
    final Scalar minSpeed = mpcOptimizationConfig.minSpeed;
    final Scalar mpcMaxSpeed = //
        optional.map(MPCDrivingAbstractModule.toMPCmaxSpeed(minSpeed, MPCLudicConfig.FERRY.maxSpeed)).orElse(minSpeed);
    return new MPCOptimizationParameterLudic( //
        mpcMaxSpeed, //
        mpcOptimizationConfig.maxLonAcc, //
        mpcOptimizationConfig.steeringReg, //
        mpcOptimizationConfig.specificMoI, //
        MPCLudicConfig.FERRY);
  }

  @Override // from MPCDrivingAbstractModule
  protected final boolean torqueBased() {
    return false;
  }

  @Override // from MPCDrivingAbstractModule
  protected final boolean powerSteeringUsed() {
    return MPCLudicConfig.GLOBAL.powerSteer;
  }
}
