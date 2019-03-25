// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.BufferInsertable;

public class ControlAndPredictionStepsMessage extends MPCNativeMessage {
  public final ControlAndPredictionSteps controlAndPredictionSteps;

  public ControlAndPredictionStepsMessage(ControlAndPredictionSteps controlAndPredictionSteps, MPCNativeSession mpcNativeSession) {
    super(mpcNativeSession);
    this.controlAndPredictionSteps = controlAndPredictionSteps;
  }

  public ControlAndPredictionStepsMessage(ByteBuffer byteBuffer) {
    super(byteBuffer);
    controlAndPredictionSteps = new ControlAndPredictionSteps(byteBuffer);
  }

  @Override // from MPCNativeMessage
  MessageType getMessageType() {
    return MessageType.CONTROL_PREDICTION;
  }

  @Override // from MPCNativeMessage
  BufferInsertable getPayload() {
    return controlAndPredictionSteps;
  }
}
