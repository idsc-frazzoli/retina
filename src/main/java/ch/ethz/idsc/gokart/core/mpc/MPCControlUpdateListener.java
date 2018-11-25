// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

public abstract class MPCControlUpdateListener {
  protected ControlAndPredictionSteps cns = null;
  int istep = 0;

  void getControlAndPredictionSteps(ControlAndPredictionSteps controlAndPredictionSteps) {
    this.cns = controlAndPredictionSteps;
  }

  /** get the last step before a point int time
   * 
   * @param query time in Unit [s]
   * @return the control and prediction step before time */
  ControlAndPredictionStep getStep(Scalar time) {
    // ensure that old data is not used
    if (cns == null || Scalars.lessThan(MPCNative.OPEN_LOOP_TIME, time.subtract(cns.steps[0].state.getTime())))
      return null;
    while (//
    istep > 0 && //
        Scalars.lessThan(//
            time, //
            cns.steps[istep].state.getTime())) {
      istep--;
    }
    while (//
    istep + 1 < cns.steps.length && //
        Scalars.lessThan(//
            cns.steps[istep + 1].state.getTime(), //
            time)) {
      istep++;
    }
    // System.out.println("time: "+time.subtract(cns.steps[0].state.getTime())+"step: "+istep);
    return cns.steps[istep];
  }

  /** the time that passed after the last step
   * 
   * @param query time in Unit [s]
   * @return time passed since that last step in Unit [s] */
  Scalar getTimeSinceLastStep(Scalar time) {
    if (cns == null)
      return null;
    return time.subtract(getStep(time).state.getTime());
  }
}
