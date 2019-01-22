// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

/* package*/ class ControlAndPredictionStepsMessage extends MPCNativeMessage {
  public final ControlAndPredictionSteps controlAndPredictionSteps;

  public ControlAndPredictionStepsMessage(ControlAndPredictionSteps controlAndPredictionSteps, MPCNativeSession mpcNativeSession) {
    super(mpcNativeSession);
    this.controlAndPredictionSteps = controlAndPredictionSteps;
  }

  public ControlAndPredictionStepsMessage(ByteBuffer byteBuffer) {
    super(byteBuffer);
    controlAndPredictionSteps = new ControlAndPredictionSteps(byteBuffer);
  }

  @Override
  public int getMessagePrefix() {
    return MPCNative.CONTROL_UPDATE;
  }

  @Override
  public MPCNativeInsertable getPayload() {
    return controlAndPredictionSteps;
  }
}
