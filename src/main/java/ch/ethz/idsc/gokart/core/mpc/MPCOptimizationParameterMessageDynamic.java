// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.BufferInsertable;

/* package */ class MPCOptimizationParameterMessageDynamic extends MPCNativeMessage {
  private final MPCOptimizationParameterDynamic mpcOptimizationParameter;

  public MPCOptimizationParameterMessageDynamic(MPCOptimizationParameterDynamic mpcOptimizationParameter, MPCNativeSession mpcNativeSession) {
    super(mpcNativeSession);
    this.mpcOptimizationParameter = mpcOptimizationParameter;
  }

  public MPCOptimizationParameterMessageDynamic(ByteBuffer byteBuffer) {
    super(byteBuffer);
    mpcOptimizationParameter = new MPCOptimizationParameterDynamic(byteBuffer);
  }

  @Override // from MPCNativeMessage
  MessageType getMessageType() {
    return MessageType.OPTIMIZATION_PARAMETER_DYNAMIC;
  }

  @Override // from MPCNativeMessage
  BufferInsertable getPayload() {
    return mpcOptimizationParameter;
  }
}
