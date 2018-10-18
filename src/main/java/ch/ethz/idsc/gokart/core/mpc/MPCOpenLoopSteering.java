package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

public class MPCOpenLoopSteering implements MPCSteering {
  ControlAndPredictionSteps cns = null;
  Scalar stateTime = RealScalar.ZERO;
  int inext = 0;
  @Override
  public void Update(ControlAndPredictionSteps controlAndPredictionSteps) {
    cns = controlAndPredictionSteps;
    inext = 0;
  }

  @Override
  public Scalar getSteering(Scalar time) {
    //find at which stage we are
    while(//
        Scalars.lessThan(//
            stateTime,//
            cns.steps[inext].state.getTime())) {
      inext++;
    }
    Scalar timeStepToCurrent = time.subtract(cns.steps[inext-1].state.getTime());
    Scalar rampUp = timeStepToCurrent.multiply(cns.steps[inext-1].control.getudotS());
    return cns.steps[inext-1].state.getS().add(rampUp);
  }
}
