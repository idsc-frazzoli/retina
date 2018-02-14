// code by jph
package ch.ethz.idsc.retina.lcm;

import java.nio.ByteBuffer;

import ch.ethz.idsc.tensor.Scalar;

public interface OfflineLogListener {
  void event(Scalar time, String channel, ByteBuffer byteBuffer);
}
