// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.nio.ByteBuffer;

public interface Hdl32eGpsPacketConsumer {
  void gps(ByteBuffer byteBuffer);
}
