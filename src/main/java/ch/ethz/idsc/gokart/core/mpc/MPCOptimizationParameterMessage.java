// code by mh, jph
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.BufferInsertable;

/* package */ abstract class MPCOptimizationParameterMessage extends MPCNativeMessage {
  protected MPCOptimizationParameter mpcOptimizationParameter;

  public MPCOptimizationParameterMessage(MPCNativeSession mpcNativeSession) {
    super(mpcNativeSession);
  }

  public MPCOptimizationParameterMessage(ByteBuffer byteBuffer) {
    super(byteBuffer);
  }

  @Override
  final BufferInsertable getPayload() {
    return mpcOptimizationParameter;
  }
}
