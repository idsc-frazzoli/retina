// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

/* package */ class ControlAndPredictionSteps implements MPCNativeInsertable {
  public final ControlAndPredictionStep[] controlAndPredictionSteps;

  public ControlAndPredictionSteps(ControlAndPredictionStep[] controlAndPredictionSteps) {
    this.controlAndPredictionSteps = controlAndPredictionSteps;
  }

  public ControlAndPredictionSteps(ByteBuffer byteBuffer) {
    controlAndPredictionSteps = new ControlAndPredictionStep[MPCNative.PREDICTIONSIZE];
    for (int i = 0; i < MPCNative.PREDICTIONSIZE; i++) {
      controlAndPredictionSteps[i] = new ControlAndPredictionStep(byteBuffer);
    }
  }

  @Override
  public void insert(ByteBuffer byteBuffer) {
    for (ControlAndPredictionStep step : controlAndPredictionSteps) {
      step.insert(byteBuffer);
    }
  }

  @Override
  public int length() {
    return controlAndPredictionSteps[0].length() * controlAndPredictionSteps.length;
  }
}
