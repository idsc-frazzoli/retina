// code by jph
package ch.ethz.idsc.retina.dev.davis;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** reads raw bytes from a source buffer, decodes them to an event.
 * the event is distributed to listeners */
public interface DavisDecoder {
  ByteOrder getByteOrder();

  void read(ByteBuffer byteBuffer);

  void read(int data, int time);

  void addListener(DavisEventListener davisEventListener);
}
