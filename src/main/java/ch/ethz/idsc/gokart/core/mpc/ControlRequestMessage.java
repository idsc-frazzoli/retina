// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.data.BufferInsertable;

/* package */ class ControlRequestMessage extends MPCNativeMessage {
  private final StateAndPath stateAndPath;

  public ControlRequestMessage(MPCNativeSession mpcNativeSession, GokartState gokartState, MPCPathParameter mpcPathParameter) {
    super(mpcNativeSession);
    this.stateAndPath = new StateAndPath(gokartState, mpcPathParameter);
  }

  @Override // from MPCNativeMessage
  MessageType getMessageType() {
    return MessageType.CONTROL_REQUEST;
  }

  @Override // from MPCNativeMessage
  BufferInsertable getPayload() {
    return stateAndPath;
  }
}
