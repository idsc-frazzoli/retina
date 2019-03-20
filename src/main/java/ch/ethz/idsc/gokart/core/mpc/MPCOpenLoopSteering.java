// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class MPCOpenLoopSteering extends MPCSteering {
  private final MPCOptimizationConfig mpcOptimizationConfig = MPCOptimizationConfig.GLOBAL;
  // TODO MH not used
  private MPCStateEstimationProvider mpcStateProvider;

  @Override // from MPCSteering
  Optional<Tensor> getSteering(Scalar time) {
    Scalar controlTime = time.add(mpcOptimizationConfig.steerAntiLag);
    ControlAndPredictionStep cnpStep = getStep(controlTime);
    if (Objects.isNull(cnpStep))
      return Optional.empty();
    Scalar timeSinceLastStep = getTimeSinceLastStep(controlTime);
    Scalar rampUp = timeSinceLastStep.multiply(cnpStep.gokartControl().getudotS());
    return Optional.of(Tensors.of( //
        cnpStep.gokartState().getS().add(rampUp), //
        cnpStep.gokartControl().getudotS()));
  }

  @Override
  public void getControlAndPredictionSteps(ControlAndPredictionSteps controlAndPredictionSteps) {
    // TODO MH default behavior, no need to override function
    cns = controlAndPredictionSteps;
  }

  @Override
  public void setStateEstimationProvider(MPCStateEstimationProvider mpcstateProvider) {
    this.mpcStateProvider = mpcstateProvider;
  }

  @Override
  public void start() {
    // TODO MH comment if empty is the right implementation
  }

  @Override
  public void stop() {
    // TODO MH comment if empty is the right implementation
  }
}
