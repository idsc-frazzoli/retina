// code by mh, jph
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Optional;

import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.red.Max;

public class MPCDrivingDynamicModule extends MPCDrivingCommonModule {
  public MPCDrivingDynamicModule() {
    super(MPCRequestPublisher.dynamic(), Timing.started());
  }

  // for testing only
  MPCDrivingDynamicModule(MPCStateEstimationProvider mpcStateEstimationProvider, Timing timing, MPCPreviewableTrack track) {
    super(MPCRequestPublisher.dynamic(), mpcStateEstimationProvider, timing, track);
  }

  @Override // from MPCAbstractDrivingModule
  MPCOptimizationParameterDynamic createOptimizationParameter( //
      MPCOptimizationConfig mpcOptimizationConfig, Optional<ManualControlInterface> optional) {
    return optimizationParameter(mpcOptimizationConfig, optional);
  }

  // package for testing
  static MPCOptimizationParameterDynamic optimizationParameter( //
      MPCOptimizationConfig mpcOptimizationConfig, Optional<ManualControlInterface> optional) {
    final Scalar minSpeed = mpcOptimizationConfig.minSpeed;
    final Scalar mpcMaxSpeed;
    if (optional.isPresent()) {
      ManualControlInterface manualControlInterface = optional.get();
      Scalar forward = manualControlInterface.getAheadPair_Unit().Get(1);
      mpcMaxSpeed = Max.of(minSpeed, mpcOptimizationConfig.maxSpeed.multiply(forward));
    } else
      mpcMaxSpeed = minSpeed; // fallback speed value
    return new MPCOptimizationParameterDynamic( //
        mpcMaxSpeed, //
        mpcOptimizationConfig.maxLonAcc, //
        mpcOptimizationConfig.steeringReg, //
        mpcOptimizationConfig.specificMoI);
  }

  @Override // from MPCDrivingAbstractModule
  protected final boolean torqueBased() {
    return false;
  }
  
  protected final boolean PowerSteeringUsed() {
    return false;
  }
}
