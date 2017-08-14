// code by jph
package ch.ethz.idsc.retina.davis.data;

import java.nio.ByteBuffer;

/** receives a timed column at given coordinate x */
public interface DavisApsColumnListener {
  void column(int x, ByteBuffer byteBuffer);
}
