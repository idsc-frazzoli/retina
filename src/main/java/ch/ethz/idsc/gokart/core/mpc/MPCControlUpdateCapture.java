// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;

import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

public class MPCControlUpdateCapture implements MPCControlUpdateListener {
  // TODO MH/JPH initialize cns with zero structure to avoid null checks
  /* package */ ControlAndPredictionSteps cns = null;
  /** istep is outside the function to reuse the value from the previous function call */
  private int istep = 0;

  /** get the last step before a point int time
   * 
   * @param query time in Unit [s]
   * @return the control and prediction step before time */
  final ControlAndPredictionStep getStep(Scalar time) {
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

  @Override // from MPCControlUpdateListener
  public final void getControlAndPredictionSteps(ControlAndPredictionSteps controlAndPredictionSteps) {
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

  private static final Scalar NO_ACCELERATION = Quantity.of(0.0, SI.ACCELERATION);
  private static final Scalar NO_STEERING = Quantity.of(0.0, SteerPutEvent.UNIT_ENCODER);

  /** get the predicted positions
   * 
   * @return predicted X- and Y-position in tensor */
  public final Tensor getPositions() {
    // avoid race conditions
    return Objects.isNull(cns) //
        ? Tensors.empty()
        : cns.toPositions();
  }

  /** get the acceleration at prediction steps */
  public final Tensor getAccelerations() {
    return Objects.isNull(cns) //
        ? Tensors.empty()
        : cns.toAccelerations();
  }

  /** @return quantity with unit "m*s^-2" */
  public final Scalar getFirstWantedAcceleration() {
    if (Objects.nonNull(cns))
      return cns.steps[0].gokartControl().getaB();
    return NO_ACCELERATION;
  }

  public final Scalar getFirstWantedSteering() {
    if (Objects.nonNull(cns))
      return cns.steps[0].gokartState().getS();
    return NO_STEERING;
  }
}
