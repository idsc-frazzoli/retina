// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.BufferInsertable;

/* package */ class MPCOptimizationParameterMessageKinematic extends MPCNativeMessage {
  private final MPCOptimizationParameter mpcOptimizationParameter;

  public MPCOptimizationParameterMessageKinematic(MPCOptimizationParameter mpcOptimizationParameter, MPCNativeSession mpcNativeSession) {
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

  @Override // from MPCNativeMessage
  BufferInsertable getPayload() {
    return mpcOptimizationParameter;
  }
}
