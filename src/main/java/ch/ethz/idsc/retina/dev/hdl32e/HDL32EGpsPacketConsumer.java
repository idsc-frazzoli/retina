// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.nio.ByteBuffer;

public interface HDL32EGpsPacketConsumer {
  void gps(ByteBuffer byteBuffer);
}
