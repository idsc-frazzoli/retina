package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

public class MPCOpenLoopSteering implements MPCSteering {
  ControlAndPredictionSteps cns = null;
  int inext = 0;

  @Override
  public Scalar getSteering(Scalar time) {
    // find at which stage we are
    while (//
    Scalars.lessThan(//
        time, //
        cns.steps[inext].state.getTime())) {
      inext++;
    }
    Scalar timeStepToCurrent = time.subtract(cns.steps[inext - 1].state.getTime());
    Scalar rampUp = timeStepToCurrent.multiply(cns.steps[inext - 1].control.getudotS());
    return cns.steps[inext - 1].state.getS().add(rampUp);
  }

  @Override
  public void getState(GokartState state) {
    // not used here
  }

  @Override
  public void getControlAndPredictionSteps(ControlAndPredictionSteps controlAndPredictionSteps) {
    cns = controlAndPredictionSteps;
    inext = 0;
  }
}
