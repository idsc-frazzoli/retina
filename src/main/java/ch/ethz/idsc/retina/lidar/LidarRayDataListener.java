// code by jph
package ch.ethz.idsc.retina.lidar;

import java.nio.ByteBuffer;

public interface LidarRayDataListener {
  /** function is invoked with parameters that refer to previous sequence of laser data
   * 
   * @param usec microseconds from the top of the hour to the first laser firing in the packet
   * @param type device dependent */
  void timestamp(int usec, int type);

  /** parameters depend on sensor
   * 
   * rotational Velodyne [0, ..., 35999] in 100th of degree Mark8 [0, ..., 10399]
   * where 10400 corresponds to 360 degrees
   * 
   * for velodyne: implementations can read LASERS * 3 bytes from byteBuffer:
   * 
   * for (int laser = 0; laser < LASERS; ++laser) { int distance =
   * byteBuffer.getShort() & 0xffff; int intensity = byteBuffer.get(); }
   * 
   * @param rotational
   * @param byteBuffer */
  void scan(int rotational, ByteBuffer byteBuffer);
}
