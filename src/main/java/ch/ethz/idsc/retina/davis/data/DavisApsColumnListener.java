// code by jph
package ch.ethz.idsc.retina.davis.data;

import java.nio.ByteBuffer;

/** receives a timed column at given coordinate x */
@FunctionalInterface
public interface DavisApsColumnListener {
  /** @param x
   * @param byteBuffer with [time + pixels] of one column byteBuffer has position set to
   * read from, typically position() == 0 */
  void column(int x, ByteBuffer byteBuffer);
}
