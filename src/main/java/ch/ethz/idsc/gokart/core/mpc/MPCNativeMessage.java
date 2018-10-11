package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

public abstract class MPCNativeMessage implements MPCNativeInsertable {
  abstract public int getMessagePrefix();

  abstract public MPCNativeInsertable getPayload();

  private final int messageSequence;

  /** it is the responsibility of the extender to initiate the payload! */
  public MPCNativeMessage(ByteBuffer byteBuffer) {
    if (getMessagePrefix() != byteBuffer.getInt()) {
      // TODO: do something!
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
  public int getLength() {
    return 8 + getPayload().getLength();
  }

  @Override
  public void input(ByteBuffer byteBuffer) {
    // just override this for different kinds of messages
    byteBuffer.putInt(getMessagePrefix());
    byteBuffer.putInt(messageSequence);
    getPayload().input(byteBuffer);
  }
}
