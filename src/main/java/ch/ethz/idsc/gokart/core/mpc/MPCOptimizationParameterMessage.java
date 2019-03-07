// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.BufferInsertable;

/* package */ class MPCOptimizationParameterMessage extends MPCNativeMessage {
  private final MPCOptimizationParameter mpcOptimizationParameter;

  public MPCOptimizationParameterMessage(MPCOptimizationParameter mpcOptimizationParameter, MPCNativeSession mpcNativeSession) {
    super(mpcNativeSession);
    this.mpcOptimizationParameter = mpcOptimizationParameter;
  }

  public MPCOptimizationParameterMessage(ByteBuffer byteBuffer) {
    super(byteBuffer);
    mpcOptimizationParameter = new MPCOptimizationParameter(byteBuffer);
  }

  @Override // from MPCNativeMessage
  MessageType getMessageType() {
    return MessageType.OPTIMIZATION_PARAMETER;
  }

  @Override // from MPCNativeMessage
  BufferInsertable getPayload() {
    return mpcOptimizationParameter;
  }
}
