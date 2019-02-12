// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class MPCCorrectedOpenLoopSteering extends MPCSteering {
  // private final MPCActiveCompensationLearning mpcActiveCompensationLearning = MPCActiveCompensationLearning.getInstance();
  private final MPCOptimizationConfig config = MPCOptimizationConfig.GLOBAL;
  // TODO MH variable not used:
  private MPCStateEstimationProvider mpcStateProvider;

  @Override
  public Optional<Tensor> getSteering(Scalar time) {
    Scalar controlTime = time.add(config.steerAntiLag);
    ControlAndPredictionStep cnpStep = getStep(controlTime);
    if (Objects.isNull(cnpStep))
      return Optional.empty();
    Scalar timeSinceLastStep = getTimeSinceLastStep(controlTime);
    Scalar rampUp = timeSinceLastStep.multiply(cnpStep.gokartControl.getudotS());
    return Optional.of(Tensors.of( //
        cnpStep.gokartState.getS().add(rampUp), //
        cnpStep.gokartControl.getudotS()));
    // .multiply(mpcActiveCompensationLearning.steeringCorrection);
  }

  // @Override
  // public Scalar getDotSteering(Scalar time) {
  // Scalar controlTime = time.add(config.steerAntiLag);
  // ControlAndPredictionStep cnpStep = getStep(controlTime);
  // if (Objects.isNull(cnpStep))
  // return null;
  // return
  // .multiply(mpcActiveCompensationLearning.steeringCorrection);
  // }
  @Override
  public void getControlAndPredictionSteps(ControlAndPredictionSteps controlAndPredictionSteps) {
    cns = controlAndPredictionSteps;
  }

  @Override
  public void setStateEstimationProvider(MPCStateEstimationProvider mpcstateProvider) {
    this.mpcStateProvider = mpcstateProvider;
  }

  @Override
  public void start() {
  }

  @Override
  public void stop() {
  }
}
