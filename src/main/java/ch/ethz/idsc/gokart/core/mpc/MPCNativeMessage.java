// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.BufferInsertable;

/* package */ abstract class MPCNativeMessage implements BufferInsertable {
  private final int messageSequence;

  /** it is the responsibility of the extender to initiate the payload! */
  public MPCNativeMessage(ByteBuffer byteBuffer) {
    int messageType = byteBuffer.getInt();
    if (getMessageType().ordinal() != messageType) {
      // TODO MH do something!
      System.err.println("unexpected " + messageType + " != " + getMessageType().ordinal());
    }
    messageSequence = byteBuffer.getInt();
  }

  /** it is the responsibility of the extender to initiate the payload! */
  public MPCNativeMessage(MPCNativeSession mpcNativeSession) {
    messageSequence = mpcNativeSession.getMessageId(this);
  }

  public final int getMessageSequence() {
    return messageSequence;
  }

  @Override // from BufferInsertable
  public final int length() {
    return 8 + getPayload().length();
  }

  @Override // from BufferInsertable
  public final void insert(ByteBuffer byteBuffer) {
    // just override this for different kinds of messages
    byteBuffer.putInt(getMessageType().ordinal());
    byteBuffer.putInt(messageSequence);
    getPayload().insert(byteBuffer);
  }

  /** @return unique identified of the message type */
  abstract MessageType getMessageType();

  /** @return message payload/content data */
  abstract BufferInsertable getPayload();
}
