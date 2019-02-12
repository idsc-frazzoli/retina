// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.BufferInsertable;

/* package */ class MPCPathParameterMessage extends MPCNativeMessage {
  public final MPCPathParameter mpcPathParameter;

  public MPCPathParameterMessage(MPCPathParameter mpcPathParameter, MPCNativeSession mpcNativeSession) {
    super(mpcNativeSession);
    this.mpcPathParameter = mpcPathParameter;
  }

  public MPCPathParameterMessage(ByteBuffer byteBuffer) {
    super(byteBuffer);
    mpcPathParameter = new MPCPathParameter(byteBuffer);
  }

  @Override // from MPCNativeMessage
  MessageType getMessageType() {
    return MessageType.PATH_PARAMETER;
  }

  @Override // from MPCNativeMessage
  BufferInsertable getPayload() {
    return mpcPathParameter;
  }
}
