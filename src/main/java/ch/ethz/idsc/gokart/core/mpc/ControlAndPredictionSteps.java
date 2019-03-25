// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;
import java.util.stream.Stream;

import ch.ethz.idsc.retina.util.data.BufferInsertable;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class ControlAndPredictionSteps implements BufferInsertable {
  final ControlAndPredictionStep[] steps;

  public ControlAndPredictionSteps(ControlAndPredictionStep[] controlAndPredictionSteps) {
    steps = controlAndPredictionSteps;
  }

  // TODO JPH can use byteBuffer.remaining() for adaptive size
  public ControlAndPredictionSteps(ByteBuffer byteBuffer) {
    steps = new ControlAndPredictionStep[MPCNative.PREDICTION_SIZE];
    for (int index = 0; index < MPCNative.PREDICTION_SIZE; ++index)
      steps[index] = new ControlAndPredictionStep(byteBuffer);
  }

  @Override // from BufferInsertable
  public void insert(ByteBuffer byteBuffer) {
    for (ControlAndPredictionStep step : steps)
      step.insert(byteBuffer);
  }

  @Override // from BufferInsertable
  public int length() {
    return ControlAndPredictionStep.LENGTH * steps.length;
  }

  /***************************************************/
  public Tensor toPositions() {
    return Tensor.of(Stream.of(steps) //
        .map(ControlAndPredictionStep::gokartState) //
        .map(GokartState::getPosition));
  }

  /** @return vector of quantities with unit "m*s^-2" */
  public Tensor toAccelerations() {
    return Tensor.of(Stream.of(steps) //
        .map(ControlAndPredictionStep::gokartControl) //
        .map(GokartControl::getaB)); //
  }

  /** @return matrix with rows of the form {x[m], y[m], psi} */
  public Tensor toXYA() {
    return Tensor.of(Stream.of(steps) //
        .map(ControlAndPredictionStep::gokartState) //
        .map(gokartState -> Tensors.of( //
            gokartState.getX(), //
            gokartState.getY(), //
            gokartState.getPsi())));
  }
}
