//code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

/* package */ class MPCOptimizationParameterMessage extends MPCNativeMessage {
  public final MPCOptimizationParameter mpcOptimizationParameter;

  public MPCOptimizationParameterMessage(MPCOptimizationParameter mpcOptimizationParameter, MPCNativeSession mpcNativeSession) {
    super(mpcNativeSession);
    this.mpcOptimizationParameter = mpcOptimizationParameter;
  }

  public MPCOptimizationParameterMessage(ByteBuffer byteBuffer) {
    super(byteBuffer);
    mpcOptimizationParameter = new MPCOptimizationParameter(byteBuffer);
  }

  @Override
  public int getMessagePrefix() {
    return MPCNative.PARAMETER_UPDATE;
  }

  @Override
  public MPCNativeInsertable getPayload() {
    return mpcOptimizationParameter;
  }
}
