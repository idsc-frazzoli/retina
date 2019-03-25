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
  public void setStateEstimationProvider(MPCStateEstimationProvider mpcstateProvider) {
    this.mpcStateProvider = mpcstateProvider;
  }
}
