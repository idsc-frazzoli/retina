// code by ta
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Optional;

import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.red.Max;

public class MPCDrivingTorqueModule extends MPCDrivingCommonModule {
  public MPCDrivingTorqueModule() {
    super(MPCRequestPublisher.torque(), Timing.started());
  }

  // for testing only
  MPCDrivingTorqueModule(MPCStateEstimationProvider mpcStateEstimationProvider, Timing timing, MPCPreviewableTrack track) {
    super(MPCRequestPublisher.torque(), mpcStateEstimationProvider, timing, track);
  }

  @Override // from MPCAbstractDrivingModule
  MPCOptimizationParameterLudic createOptimizationParameter( //
      MPCOptimizationConfig mpcOptimizationConfig, Optional<ManualControlInterface> optional) {
    return optimizationParameter(mpcOptimizationConfig, optional);
  }

  static MPCOptimizationParameterLudic optimizationParameter( //
      MPCOptimizationConfig mpcOptimizationConfig, Optional<ManualControlInterface> optional) {
    final Scalar maxSpeed = MPCLudicConfig.FERRY.maxSpeed;
    final Scalar minSpeed = mpcOptimizationConfig.minSpeed;
    final Scalar mpcMaxSpeed;
    if (optional.isPresent()) {
      ManualControlInterface manualControlInterface = optional.get();
      Scalar forward = manualControlInterface.getAheadPair_Unit().Get(1);
      mpcMaxSpeed = Max.of(minSpeed, maxSpeed.multiply(forward));
    } else
      mpcMaxSpeed = minSpeed; // fallback speed value
    MPCOptimizationParameterLudic mpcOptimizationParameterLudic = new MPCOptimizationParameterLudic( //
        mpcMaxSpeed, //
        mpcOptimizationConfig.maxLonAcc, //
        mpcOptimizationConfig.steeringReg, //
        mpcOptimizationConfig.specificMoI, //
        MPCLudicConfig.FERRY);
    return mpcOptimizationParameterLudic;
  }

  @Override // from MPCDrivingAbstractModule
  protected final boolean torqueBased() {
    return true;
  }
  
  protected final boolean PowerSteeringUsed() {
    return MPCLudicConfig.GLOBAL.powerSteer;
  }
}
