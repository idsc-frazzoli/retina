// code by jph
package ch.ethz.idsc.retina.davis.data;

import java.nio.ByteBuffer;

/** notifies that block of aps columns is completed */
@FunctionalInterface
public interface DavisDvsBlockListener {
  void dvsBlock(int length, ByteBuffer byteBuffer);
}
