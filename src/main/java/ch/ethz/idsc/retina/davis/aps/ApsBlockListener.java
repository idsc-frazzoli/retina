// code by jph
package ch.ethz.idsc.retina.davis.aps;

import java.nio.ByteBuffer;

/** notifies that block of aps columns is completed */
public interface ApsBlockListener {
  void apsBlock(int length, ByteBuffer byteBuffer);
}
