// code by jph
package ch.ethz.idsc.retina.dev.vlp16;

import java.nio.ByteBuffer;

public interface Vlp16RayDataListener {
  static final int LASERS = 32;

  /** function is invoked with parameters that refer to previous sequence of laser data
   * 
   * @param usec gps timestamp in microseconds
   * @param type
   * @param value */
  void timestamp(int usec, byte type, byte value);

  /** implementations can read 32 * 3 == 96 bytes from byteBuffer:
   * 
   * for (int laser = 0; laser < LASERS; ++laser) {
   * int distance = byteBuffer.getShort() & 0xffff;
   * int intensity = byteBuffer.get();
   * }
   * 
   * @param rotational [0, ..., 35999]
   * @param byteBuffer */
  void scan(int rotational, ByteBuffer byteBuffer);
}
