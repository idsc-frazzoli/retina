// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.BufferInsertable;

/* package */ class ControlAndPredictionSteps implements BufferInsertable {
  public final ControlAndPredictionStep[] steps;

  public ControlAndPredictionSteps(ControlAndPredictionStep[] controlAndPredictionSteps) {
    steps = controlAndPredictionSteps;
  }

  // TODO can use byteBuffer.remaining() for adaptive size
  public ControlAndPredictionSteps(ByteBuffer byteBuffer) {
    steps = new ControlAndPredictionStep[MPCNative.PREDICTION_SIZE];
    for (int index = 0; index < MPCNative.PREDICTION_SIZE; ++index)
      steps[index] = new ControlAndPredictionStep(byteBuffer);
  }

  @Override
  public void insert(ByteBuffer byteBuffer) {
    for (ControlAndPredictionStep step : steps)
      step.insert(byteBuffer);
  }

  @Override
  public int length() {
    return steps[0].length() * steps.length;
  }
}
