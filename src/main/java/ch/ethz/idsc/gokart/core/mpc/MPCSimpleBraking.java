package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

public class MPCSimpleBraking implements MPCBraking {
  ControlAndPredictionSteps cns;
  MPCStateProvider mpcStateProvider;
  int inext = 0;

  @Override
  public Scalar getBraking(Scalar time) {
    // find at which stage we are
    while (//
    Scalars.lessThan(//
        time, //
        cns.steps[inext].state.getTime())) {
      inext++;
    }
    if (inext > 0) {
      return cns.steps[inext - 1].control.getuB();
    }
    return null;
  }

  @Override
  public void getControlAndPredictionSteps(ControlAndPredictionSteps controlAndPredictionSteps) {
    this.cns = controlAndPredictionSteps;
    inext = 0;
  }
  
  @Override
  public void setStateProvider(MPCStateProvider mpcstateProvider) {
    this.mpcStateProvider = mpcstateProvider;
  }
}
