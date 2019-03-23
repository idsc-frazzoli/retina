// code by jph
package ch.ethz.idsc.retina.util.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** base class for all data that is sent and received between the autobox and the pc */
public abstract class DataEvent implements DataEventInterface {
  /** @return byte array with content of this data event */
  @Override
  public final byte[] asArray() {
    byte[] data = new byte[length()];
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    insert(byteBuffer);
    return data;
  }
}
