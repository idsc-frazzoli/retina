// code by jph
package ch.ethz.idsc.retina.davis.io.dvs;

import java.nio.ByteBuffer;

/** notifies that block of aps columns is completed */
public interface DvsBlockListener {
  void dvsBlockReady(int length, ByteBuffer byteBuffer);
}
