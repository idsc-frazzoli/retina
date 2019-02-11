// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.BufferInsertable;

/* package */ class ControlRequestMessage extends MPCNativeMessage {
  public final StateAndPath stateAndPath;

  public ControlRequestMessage(GokartState gokartState, MPCPathParameter mpcPathParameter, MPCNativeSession mpcNativeSession) {
    super(mpcNativeSession);
    this.stateAndPath = new StateAndPath(mpcPathParameter, gokartState);
  }

  public ControlRequestMessage(ByteBuffer byteBuffer) {
    super(byteBuffer);
    stateAndPath = new StateAndPath(byteBuffer);
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
