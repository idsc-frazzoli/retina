// code by jph
package ch.ethz.idsc.retina.dev.zhkart;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class DataEvent implements Serializable {
  public final byte[] asArray() {
    byte[] data = new byte[length()];
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    insert(byteBuffer);
    return data;
  }

  /** @return number of bytes required to encode this event */
  protected abstract int length();

  /** @param byteBuffer to which the event is appended */
  protected abstract void insert(ByteBuffer byteBuffer);
}
