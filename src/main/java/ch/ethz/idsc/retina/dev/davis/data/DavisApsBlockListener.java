// code by jph
package ch.ethz.idsc.retina.dev.davis.data;

import java.nio.ByteBuffer;

/** notifies that block of aps columns is completed */
public interface DavisApsBlockListener {
  /** @param byteBuffer
   * with capacity 2 + COLUMNS * [time + pixels] */
  void apsBlock(ByteBuffer byteBuffer);
}
