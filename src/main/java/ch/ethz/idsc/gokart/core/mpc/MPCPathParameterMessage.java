// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.BufferInsertable;

/* package */ class MPCPathParameterMessage extends MPCNativeMessage {
  public final MPCPathParameter mpcPathParameters;

  public MPCPathParameterMessage(MPCPathParameter mpcPathParameters, MPCNativeSession mpcNativeSession) {
    super(mpcNativeSession);
    this.mpcPathParameters = mpcPathParameters;
  }

  public MPCPathParameterMessage(ByteBuffer byteBuffer) {
    super(byteBuffer);
    mpcPathParameters = new MPCPathParameter(byteBuffer);
  }

  @Override // from MPCNativeMessage
  MessageType getMessageType() {
    return MessageType.PATH_PARAMETER;
  }

  @Override // from MPCNativeMessage
  BufferInsertable getPayload() {
    return mpcPathParameters;
  }
}
