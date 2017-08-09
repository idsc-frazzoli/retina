// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.nio.ByteBuffer;

public interface Hdl32eFiringPacketListener {
  static final int LASERS = 32;

  /** implementations can read 96 bytes from byteBuffer:
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

  /** function is invoked with parameters that refer to previous sequence of laser data
   * 
   * @param usec gps timestamp in microseconds
   * @param type
   * @param value */
  void status(int usec, byte type, byte value);
}
