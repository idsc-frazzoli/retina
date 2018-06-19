// code by jph
package ch.ethz.idsc.retina.util.data;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** base class for all data that is sent and received between the autobox and the pc */
public abstract class DataEvent implements OfflineVectorInterface, Serializable {
  /** @return byte array with content of this data event */
  public final byte[] asArray() {
    byte[] data = new byte[length()];
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    insert(byteBuffer);
    return data;
  }

  /** @return number of bytes required to encode this event */
  protected abstract int length();

  /** @param byteBuffer to which the event is appended with {@link ByteOrder#LITTLE_ENDIAN} */
  protected abstract void insert(ByteBuffer byteBuffer);
}
