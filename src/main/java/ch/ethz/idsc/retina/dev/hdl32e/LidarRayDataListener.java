// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.nio.ByteBuffer;

public interface LidarRayDataListener {
  /** function is invoked with parameters that refer to previous sequence of laser data
   * 
   * @param usec microseconds from the top of the hour to the first laser firing in the packet
   * @param type */
  void timestamp(int usec, byte type);

  /** implementations can read LASERS * 3 bytes from byteBuffer:
   * 
   * for (int laser = 0; laser < LASERS; ++laser) {
   * int distance = byteBuffer.getShort() & 0xffff;
   * int intensity = byteBuffer.get();
   * }
   * 
   * @param rotational [0, ..., 35999] in 100th of degree
   * @param byteBuffer */
  void scan(int rotational, ByteBuffer byteBuffer);
}
