// code by mh
package ch.ethz.idsc.retina.util.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public interface BufferInsertable {
  /** @param byteBuffer to which the event is appended with {@link ByteOrder#LITTLE_ENDIAN} */
  void insert(ByteBuffer byteBuffer);

  /** @return number of bytes required to encode this event */
  int length();
}
