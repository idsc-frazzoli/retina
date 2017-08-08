// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.nio.ByteBuffer;

public interface FiringPacketInterface {
  static final int LASERS = 32;

  /** implementations have to advance byteBuffer by 96 bytes:
   * 
   * for (int laser = 0; laser < LASERS; ++laser) {
   * int distance = byteBuffer.getShort() & 0xffff;
   * int intensity = byteBuffer.get();
   * }
   * 
   * @param firing ranges from [0, ..., 11]
   * @param rotational [0, ..., 35999]
   * @param byteBuffer */
  void process(int firing, int rotational, ByteBuffer byteBuffer);

  /** @param usec gps timestamp in milliseconds
   * @param type
   * @param value */
  void status(int usec, byte type, byte value);
}
