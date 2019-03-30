// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

public class ControlAndPredictionStepsMessage extends MPCNativeMessage {
  /** access to field controlAndPredictionSteps via function getPayload() */
  private final ControlAndPredictionSteps controlAndPredictionSteps;

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
  public ControlAndPredictionSteps getPayload() {
    return controlAndPredictionSteps;
  }
}
