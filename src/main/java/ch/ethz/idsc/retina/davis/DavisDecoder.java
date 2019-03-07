// code by jph
package ch.ethz.idsc.retina.davis;

import java.nio.ByteBuffer;

/** reads raw bytes from a source buffer, decodes them to an event. the event is
 * distributed to listeners */
public interface DavisDecoder {
  /** in raw data format, a davis event consist of 8 bytes
   * 
   * @param byteBuffer from which 8 bytes of raw data can be read */
  void read(ByteBuffer byteBuffer);

  /** in raw data format, a davis event is encoded as two 32-bit integers
   * 
   * @param data
   * @param time */
  void read(int data, int time);

  /** @param listener to subscribe to imu events */
  void addImuListener(DavisImuListener listener);

  /** @param listener to subscribe to dvs events */
  void addDvsListener(DavisDvsListener listener);

  /** @param listener to subscribe to reset events */
  void addRstListener(DavisApsListener listener);

  /** @param listener to subscribe to signal events */
  void addSigListener(DavisApsListener listener);
}
