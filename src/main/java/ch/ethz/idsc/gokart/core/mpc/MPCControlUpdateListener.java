// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

/* package */ class MPCControlUpdateListener implements MPCControlUpdateInterface {
  /* package */ ControlAndPredictionSteps cns = null;
  // TODO MH document that keeping istep outside the function is intended
  private int istep = 0;

  /** get the last step before a point int time
   * 
   * @param query time in Unit [s]
   * @return the control and prediction step before time */
  ControlAndPredictionStep getStep(Scalar time) {
    // ensure that old data is not used
    // condition always holds: cns.steps.length == 30
    if (Objects.isNull(cns) || //
        cns.steps.length == 0 || //
        Scalars.lessThan(MPCNative.OPEN_LOOP_TIME, time.subtract(cns.steps[0].gokartState().getTime())))
      return null;
    istep = Math.min(istep, cns.steps.length - 1);
    while (istep > 0 //
        && Scalars.lessThan( //
            time, //
            cns.steps[istep].gokartState().getTime()))
      --istep;
    // ---
    while (istep + 1 < cns.steps.length //
        && Scalars.lessThan( //
            cns.steps[istep + 1].gokartState().getTime(), //
            time))
      ++istep;
    // ---
    // System.out.println("time: "+time.subtract(cns.steps[0].state.getTime())+"step: "+istep);
    return cns.steps[istep];
  }

  // TODO MH/JPH ideally this function should be final
  @Override // from MPCControlUpdateInterface
  public void getControlAndPredictionSteps(ControlAndPredictionSteps controlAndPredictionSteps) {
    this.cns = controlAndPredictionSteps;
  }

  /** the time that passed after the last step
   * 
   * @param query time in unit [s]
   * @return time passed since that last step in unit [s], or null */
  final Scalar getTimeSinceLastStep(Scalar time) {
    if (Objects.isNull(cns))
      return null;
    return time.subtract(getStep(time).gokartState().getTime());
  }
}
