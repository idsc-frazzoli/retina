// code by jph
package ch.ethz.idsc.retina.dev.davis;

import java.nio.ByteBuffer;

/** reads raw bytes from a source buffer, decodes them to an event.
 * the event is distributed to listeners */
public interface DavisDecoder extends DvsReference, ApsReference {
  void read(ByteBuffer byteBuffer);

  void addListener(DavisEventListener davisEventListener);
}
