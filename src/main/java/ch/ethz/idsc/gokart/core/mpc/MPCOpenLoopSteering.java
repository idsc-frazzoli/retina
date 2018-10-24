// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

public class MPCOpenLoopSteering extends MPCSteering  {
  MPCStateEstimationProvider mpcStateProvider;

  @Override
  public Scalar getSteering(Scalar time) {
    ControlAndPredictionStep cnpStep = getStep(time);
    Scalar timeSinceLastStep = getTimeSinceLastStep(time);
    Scalar rampUp = timeSinceLastStep.multiply(cnpStep.control.getudotS());
    return cnpStep.state.getS().add(rampUp);
  }

  @Override
  public void getControlAndPredictionSteps(ControlAndPredictionSteps controlAndPredictionSteps) {
    cns = controlAndPredictionSteps;
  }

  @Override
  public void setStateProvider(MPCStateEstimationProvider mpcstateProvider) {
    this.mpcStateProvider = mpcstateProvider;
  }
}
