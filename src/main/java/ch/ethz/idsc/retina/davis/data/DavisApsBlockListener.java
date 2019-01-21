// code by jph
package ch.ethz.idsc.retina.davis.data;

import java.nio.ByteBuffer;

/** notifies that block of aps columns is completed */
@FunctionalInterface
public interface DavisApsBlockListener {
  /** @param byteBuffer
   * with capacity 2 + COLUMNS * [time + pixels] */
  void apsBlock(ByteBuffer byteBuffer);
}
