// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

/* package */ class MPCOptimizationParameterMessageLudic extends MPCOptimizationParameterMessage {
  public MPCOptimizationParameterMessageLudic( //
      MPCNativeSession mpcNativeSession, //
      MPCOptimizationParameterLudic mpcOptimizationParameterLudic) {
    super(mpcNativeSession);
    mpcOptimizationParameter = mpcOptimizationParameterLudic;
  }

  public MPCOptimizationParameterMessageLudic(ByteBuffer byteBuffer) {
    super(byteBuffer); // constructor reads 8 bytes from byte buffer
    mpcOptimizationParameter = new MPCOptimizationParameterLudic(byteBuffer);
  }

  @Override // from MPCNativeMessage
  MessageType getMessageType() {
    return MessageType.OPTIMIZATION_PARAMETER_LUDIC;
  }
}
