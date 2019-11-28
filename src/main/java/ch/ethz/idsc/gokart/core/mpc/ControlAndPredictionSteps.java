// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;
import java.util.stream.Stream;

import ch.ethz.idsc.retina.util.data.BufferInsertable;
import ch.ethz.idsc.tensor.Tensor;

public class ControlAndPredictionSteps implements BufferInsertable {
  final ControlAndPredictionStep[] steps;

  /** DO NOT call this construction on content of log files!
   * instead use {@link ControlAndPredictionStepsMessage}.
   * 
   * @param byteBuffer */
  /* package */ ControlAndPredictionSteps(ByteBuffer byteBuffer) {
    // TODO JPH can use byteBuffer.remaining() for adaptive size
    steps = new ControlAndPredictionStep[MPCNative.PREDICTION_SIZE];
    for (int index = 0; index < MPCNative.PREDICTION_SIZE; ++index)
      steps[index] = new ControlAndPredictionStep(byteBuffer);
  }

  /** ONLY FOR TESTING
   * 
   * @param controlAndPredictionSteps */
  /* package */ ControlAndPredictionSteps(ControlAndPredictionStep[] controlAndPredictionSteps) {
    steps = controlAndPredictionSteps;
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
  /** @return matrix of size N x 2, rows encode xy-positions {x[m], y[m]} */
  public Tensor toPositions() {
    return Tensor.of(Stream.of(steps) //
        .map(ControlAndPredictionStep::gokartState) //
        .map(GokartState::getPositionXY));
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
        .map(GokartState::getPose));
  }

  /** @return */
  public Tensor asMatrix() {
    return Tensor.of(Stream.of(steps) //
        .map(ControlAndPredictionStep::asVector));
  }
  


}
