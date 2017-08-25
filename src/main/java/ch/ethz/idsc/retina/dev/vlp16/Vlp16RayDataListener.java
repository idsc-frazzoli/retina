// code by jph
package ch.ethz.idsc.retina.dev.vlp16;

import java.nio.ByteBuffer;

public interface Vlp16RayDataListener {
  static final int LASERS = 16;

  /** function is invoked with parameters that refer to previous sequence of laser data
   * 
   * @param usec microseconds from the top of the hour to the first laser firing in the packet
   * @param type
   * @param value */
  void timestamp(int usec, byte type);

  /** implementations can read 32 * 3 == 96 bytes from byteBuffer:
   * 
   * for (int laser = 0; laser < LASERS; ++laser) {
   * int distance = byteBuffer.getShort() & 0xffff;
   * int intensity = byteBuffer.get();
   * }
   * 
   * @param azimuth [0, ..., 35999] in 100th of degree
   * @param byteBuffer */
  void scan(int azimuth, ByteBuffer byteBuffer);
}
