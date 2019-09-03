// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

/* package */ class MPCOptimizationParameterMessageDynamic extends MPCOptimizationParameterMessage {
  public MPCOptimizationParameterMessageDynamic(MPCNativeSession mpcNativeSession, MPCOptimizationParameter mpcOptimizationParameter) {
    super(mpcNativeSession);
    this.mpcOptimizationParameter = mpcOptimizationParameter;
  }

  public MPCOptimizationParameterMessageDynamic(ByteBuffer byteBuffer) {
    super(byteBuffer); // constructor reads 8 bytes from byte buffer
    mpcOptimizationParameter = new MPCOptimizationParameterDynamic(byteBuffer);
  }

  @Override // from MPCNativeMessage
  MessageType getMessageType() {
    return MessageType.OPTIMIZATION_PARAMETER_DYNAMIC;
  }
}
