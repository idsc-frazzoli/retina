// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;

import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/** singleton instance */
public class MPCInformationProvider extends MPCControlUpdateListener {
  private final static MPCInformationProvider INSTANCE = new MPCInformationProvider();
  private final static Scalar NO_ACCELERATION = Quantity.of(0, SI.ACCELERATION);
  private final static Scalar NO_STEERING = Quantity.of(0, SteerPutEvent.UNIT_ENCODER);

  public static MPCInformationProvider getInstance() {
    return INSTANCE;
  }

  private MPCInformationProvider() {
    // ---
  }

  /** get the predicted positions
   * 
   * @return predicted X- and Y-position in tensor */
  public Tensor getPositions() {
    // avoid race conditions
    return Objects.isNull(cns) //
        ? Tensors.empty()
        : toPositions(cns);
  }

  public static Tensor toPositions(ControlAndPredictionSteps controlAndPredictionSteps) {
    Tensor positions = Tensors.empty();
    if (Objects.nonNull(controlAndPredictionSteps)) {
    for (ControlAndPredictionStep step : controlAndPredictionSteps.steps)
      positions.append( //
          Tensors.of( //
                  step.gokartState.getX(), //
                  step.gokartState.getY()));
    }
    return positions;
  }

  /** get the acceleration at prediction steps */
  public Tensor getAccelerations() {
    return Objects.isNull(cns) ? Tensors.empty() : toAccelerations(cns);
  }

  public static Tensor toAccelerations(ControlAndPredictionSteps controlAndPredictionSteps) {
    Tensor accelerations = Tensors.empty();
    for (ControlAndPredictionStep step : controlAndPredictionSteps.steps)
      accelerations.append(step.gokartControl.getaB());
    return accelerations;
  }

  public Boolean mpcAvailable() {
    return Objects.nonNull(cns);
  }

  public Scalar getFirstWantedAcceleration() {
    if (Objects.nonNull(cns))
      return cns.steps[0].gokartControl.getaB();
    return NO_ACCELERATION;
  }

  public Scalar getFirstWantedSteering() {
    if (Objects.nonNull(cns))
      return cns.steps[0].gokartState.getS();
    return NO_STEERING;
  }

  /** get the poses at steps in {x,y,a} */
  public Tensor getXYA() {
    return Objects.isNull(cns) //
        ? Tensors.empty()
        : toXYA(cns);
  }

  public static Tensor toXYA(ControlAndPredictionSteps controlAndPredictionSteps) {
    Tensor orientations = Tensors.empty();
    for (ControlAndPredictionStep step : controlAndPredictionSteps.steps) {
      Scalar X = RealScalar.of(step.gokartState.getX().number().doubleValue());
      Scalar Y = RealScalar.of(step.gokartState.getY().number().doubleValue());
      orientations.append( //
          Tensors.of( //
              X, //
              Y, //
              step.gokartState.getPsi()));
    }
    return orientations;
  }

  @Override
  public void start() {
    // TODO MH document that empty implementation is desired
  }

  @Override
  public void stop() {
    // TODO MH document that empty implementation is desired
  }
}
