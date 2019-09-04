// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

/* package */ class MPCOptimizationParameterMessageKinematic extends MPCOptimizationParameterMessage {
  public MPCOptimizationParameterMessageKinematic( //
      MPCNativeSession mpcNativeSession, //
      MPCOptimizationParameterKinematic mpcOptimizationParameterKinematic) {
    super(mpcNativeSession);
    mpcOptimizationParameter = mpcOptimizationParameterKinematic;
  }

  public MPCOptimizationParameterMessageKinematic(ByteBuffer byteBuffer) {
    super(byteBuffer); // constructor reads 8 bytes from byte buffer
    mpcOptimizationParameter = new MPCOptimizationParameterKinematic(byteBuffer);
  }

  @Override // from MPCNativeMessage
  MessageType getMessageType() {
    return MessageType.OPTIMIZATION_PARAMETER_KINEMATIC;
  }
}
