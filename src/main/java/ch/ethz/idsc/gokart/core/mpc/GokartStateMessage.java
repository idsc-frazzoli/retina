// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

public class GokartStateMessage extends MPCNativeMessage {
  public final GokartState gokartState;

  public GokartStateMessage(GokartState gokartState, MPCNativeSession mpcNativeSession) {
    super(mpcNativeSession);
    this.gokartState = gokartState;
  }

  public GokartStateMessage(ByteBuffer byteBuffer) {
    super(byteBuffer);
    gokartState = new GokartState(byteBuffer);
  }

  @Override
  public int getMessagePrefix() {
    return MPCNative.GOKART_STATE;
  }

  @Override
  public MPCNativeInsertable getPayload() {
    return gokartState;
  }
}
