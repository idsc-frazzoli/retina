// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

/* package*/ class ControlRequestMessage extends MPCNativeMessage {
  public final StateAndPath stateAndPath;

  public ControlRequestMessage(GokartState gokartState, MPCPathParameter mpcPathParameter, MPCNativeSession mpcNativeSession) {
    super(mpcNativeSession);
    this.stateAndPath = new StateAndPath(mpcPathParameter, gokartState);
  }

  public ControlRequestMessage(ByteBuffer byteBuffer) {
    super(byteBuffer);
    stateAndPath = new StateAndPath(byteBuffer);
  }

  @Override
  public int getMessagePrefix() {
    return MPCNative.GOKART_STATE;
  }

  @Override
  public MPCNativeInsertable getPayload() {
    return stateAndPath;
  }
}
