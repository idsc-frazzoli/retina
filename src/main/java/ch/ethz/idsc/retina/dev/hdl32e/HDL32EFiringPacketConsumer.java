// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.nio.ByteBuffer;

public interface HDL32EFiringPacketConsumer {
  // void lasers(byte[] laser_data);
  void lasers(ByteBuffer byteBuffer);
}
