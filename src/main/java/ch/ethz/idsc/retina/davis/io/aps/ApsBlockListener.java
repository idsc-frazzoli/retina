// code by jph
package ch.ethz.idsc.retina.davis.io.aps;

import java.nio.ByteBuffer;

/** notifies that block of aps columns is completed */
public interface ApsBlockListener {
  void apsBlockReady(int length, ByteBuffer byteBuffer);
}
