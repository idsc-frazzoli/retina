// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

/* package */ class MPCOptimizationParameterMessageKinematic extends MPCOptimizationParameterMessage {
  public MPCOptimizationParameterMessageKinematic(MPCNativeSession mpcNativeSession, MPCOptimizationParameter mpcOptimizationParameter) {
    super(mpcNativeSession);
    this.mpcOptimizationParameter = mpcOptimizationParameter;
  }

  public MPCOptimizationParameterMessageKinematic(ByteBuffer byteBuffer) {
    super(byteBuffer);
    mpcOptimizationParameter = new MPCOptimizationParameterKinematic(byteBuffer);
  }

  @Override // from MPCNativeMessage
  MessageType getMessageType() {
    return MessageType.OPTIMIZATION_PARAMETER_KINEMATIC;
  }
}
