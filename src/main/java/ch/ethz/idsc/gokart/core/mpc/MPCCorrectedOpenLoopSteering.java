// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

// TODO JPH simplify! since most factors are 1 and will remain 1
/* package */ class MPCCorrectedOpenLoopSteering extends MPCSteering {
  private final MPCOptimizationConfig mpcOptimizationConfig = MPCOptimizationConfig.GLOBAL;

  @Override // from MPCSteering
  Optional<Tensor> getSteering(Scalar time) {
    Scalar controlTime = time.add(mpcOptimizationConfig.steerAntiLag);
    ControlAndPredictionStep cnpStep = getStep(controlTime);
    if (Objects.isNull(cnpStep))
      return Optional.empty();
    Scalar timeSinceLastStep = getTimeSinceLastStep(controlTime);
    Scalar rampUp = timeSinceLastStep.multiply(cnpStep.gokartControl().getudotS());
    Scalar dotFactor = mpcOptimizationConfig.steerDamp;
    return Optional.of(Tensors.of( //
        cnpStep.gokartState().getS().add(rampUp), // [SCE]
        cnpStep.gokartControl().getudotS().multiply(dotFactor)) // [SCE*s^-1]
        .multiply(mpcOptimizationConfig.steerMultiplicator));
  }
}
