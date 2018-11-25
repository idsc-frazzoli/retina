// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

public class ControlAndPredictionSteps implements MPCNativeInsertable {
  public final ControlAndPredictionStep[] steps;

  public ControlAndPredictionSteps(ControlAndPredictionStep[] controlAndPredictionSteps) {
    steps = controlAndPredictionSteps;
  }

  // TODO can use byteBuffer.remaining() for adaptive size
  public ControlAndPredictionSteps(ByteBuffer byteBuffer) {
    steps = new ControlAndPredictionStep[MPCNative.PREDICTIONSIZE];
    for (int i = 0; i < MPCNative.PREDICTIONSIZE; ++i)
      steps[i] = new ControlAndPredictionStep(byteBuffer);
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
