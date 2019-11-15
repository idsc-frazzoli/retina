// code by mh, jph
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Optional;

import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.red.Max;

public class MPCLudicDrivingModule extends MPCAbstractDrivingModule {
  public MPCLudicDrivingModule() {
    super(MPCRequestPublisher.ludic(), Timing.started());
  }

  // for testing only
  MPCLudicDrivingModule(MPCStateEstimationProvider mpcStateEstimationProvider, Timing timing, MPCPreviewableTrack track) {
    super(MPCRequestPublisher.ludic(), mpcStateEstimationProvider, timing, track);
  }

  // TODO refactor with dynamic driving module
  @Override // from MPCAbstractDrivingModule
  MPCPower createPower(MPCStateEstimationProvider mpcStateEstimationProvider, MPCSteering mpcSteering) {
    return new MPCExplicitTorqueVectoringPower();
  }

  @Override // from MPCAbstractDrivingModule
  MPCOptimizationParameterLudic createOptimizationParameter( //
      MPCOptimizationConfig mpcOptimizationConfig, Optional<ManualControlInterface> optional) {
    return optimizationParameter(mpcOptimizationConfig, optional);
  }

  static MPCOptimizationParameterLudic optimizationParameter( //
      MPCOptimizationConfig mpcOptimizationConfig, Optional<ManualControlInterface> optional) {
    final Scalar maxSpeed = mpcOptimizationConfig.maxSpeed;
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
        mpcOptimizationConfig.specificMoI);
    // mpcOptimizationParameterLudic.lagError=RealScalar.of(1);
    synchronized (MPCLudicConfig.GLOBAL) {
      mpcOptimizationParameterLudic.speedCost = MPCLudicConfig.GLOBAL.speedCost;
    }
    return mpcOptimizationParameterLudic;
  }
}
