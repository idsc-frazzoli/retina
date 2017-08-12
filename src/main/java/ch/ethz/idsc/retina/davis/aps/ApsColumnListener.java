// code by jph
package ch.ethz.idsc.retina.davis.aps;

import java.nio.ByteBuffer;

/** receives a timed column at given coordinate x */
public interface ApsColumnListener {
  void column(int x, ByteBuffer byteBuffer);
}
