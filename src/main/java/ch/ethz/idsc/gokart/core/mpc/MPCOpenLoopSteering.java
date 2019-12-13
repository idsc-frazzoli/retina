// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class MPCOpenLoopSteering extends MPCSteering {
  private final MPCOptimizationConfig mpcOptimizationConfig = MPCOptimizationConfig.GLOBAL;

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
  Optional<Tensor> getSteeringTorque(Scalar time) {
    Scalar controlTime = time.add(mpcOptimizationConfig.steerAntiLag);
    ControlAndPredictionStep cnpStep = getStep(controlTime);
    if (Objects.isNull(cnpStep))
      return Optional.empty();
    Scalar timeSinceLastStep = getTimeSinceLastStep(controlTime);
    Scalar rampUp = timeSinceLastStep.multiply(cnpStep.gokartControl().getudotT());
    return Optional.of(Tensors.of( //
        cnpStep.gokartState().getTau().add(rampUp), //
        cnpStep.gokartControl().getudotT()));
  }
  Optional<Tensor> getState(Scalar time) {
    Scalar controlTime = time.add(mpcOptimizationConfig.steerAntiLag);
    ControlAndPredictionStep cnpStep = getStep(controlTime);
    if (Objects.isNull(cnpStep))
      return Optional.empty();
    Scalar timeSinceLastStep = getTimeSinceLastStep(controlTime);
    Scalar rampUp = timeSinceLastStep.multiply(cnpStep.gokartControl().getudotT());
    Scalar rampUpS = timeSinceLastStep.multiply(cnpStep.gokartControl().getudotS());
    Tensor vel = cnpStep.gokartState().getVelocity();
    Tensor pose = cnpStep.gokartState().getPose();
    return Optional.of(Tensors.of(//
        vel,//
        pose,//
        cnpStep.gokartState().getTau().add(rampUp), //
        cnpStep.gokartControl().getudotT(),
        cnpStep.gokartState().getS().add(rampUpS), //
        cnpStep.gokartControl().getudotS()));
        
  }
  
  
}
