// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class MPCOpenLoopSteering extends MPCSteering {
  MPCStateEstimationProvider mpcStateProvider;
  MPCOptimizationConfig config = MPCOptimizationConfig.GLOBAL;

  @Override
  public Optional<Tensor> getSteering(Scalar time) {
    Scalar controlTime = time.add(config.steerAntiLag);
    ControlAndPredictionStep cnpStep = getStep(controlTime);
    if (Objects.isNull(cnpStep))
      return Optional.empty();
    Scalar timeSinceLastStep = getTimeSinceLastStep(controlTime);
    Scalar rampUp = timeSinceLastStep.multiply(cnpStep.control.getudotS());
    return Optional.of(Tensors.of(cnpStep.state.getS().add(rampUp), cnpStep.control.getudotS()));
  }

  // @Override
  // public Scalar getDotSteering(Scalar time) {
  // Scalar controlTime = time.add(config.steerAntiLag);
  // ControlAndPredictionStep cnpStep = getStep(controlTime);
  // if (Objects.isNull(cnpStep))
  // return null;
  // return
  // }
  @Override
  public void getControlAndPredictionSteps(ControlAndPredictionSteps controlAndPredictionSteps) {
    cns = controlAndPredictionSteps;
  }

  @Override
  public void setStateProvider(MPCStateEstimationProvider mpcstateProvider) {
    this.mpcStateProvider = mpcstateProvider;
  }

  @Override
  public void start() {
  }

  @Override
  public void stop() {
  }
}
