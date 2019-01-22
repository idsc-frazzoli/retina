// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

/* package */ abstract class MPCNativeMessage implements MPCNativeInsertable {
  public abstract int getMessagePrefix();

  public abstract MPCNativeInsertable getPayload();

  private final int messageSequence;

  /** it is the responsibility of the extender to initiate the payload! */
  public MPCNativeMessage(ByteBuffer byteBuffer) {
    if (getMessagePrefix() != byteBuffer.getInt()) {
      // TODO do something!
    }
    messageSequence = byteBuffer.getInt();
  }

  /** it is the responsibility of the extender to initiate the payload! */
  public MPCNativeMessage(MPCNativeSession mpcNativeSession) {
    messageSequence = mpcNativeSession.getMessageId(this);
  }

  public int getMessageSequence() {
    return messageSequence;
  }

  @Override
  public int length() {
    return 8 + getPayload().length();
  }

  @Override
  public void insert(ByteBuffer byteBuffer) {
    // just override this for different kinds of messages
    byteBuffer.putInt(getMessagePrefix());
    byteBuffer.putInt(messageSequence);
    getPayload().insert(byteBuffer);
  }
}
