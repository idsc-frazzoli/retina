// code by jph
package ch.ethz.idsc.retina.imu.vmu931;

import java.nio.ByteBuffer;

public interface Vmu931Listener {
  /** @param byteBuffer with 16 bytes to read
   * int timestamp_ms
   * float acc_x
   * float acc_y
   * float acc_z */
  void accelerometer(ByteBuffer byteBuffer);

  /** @param byteBuffer with 16 bytes to read
   * int timestamp_ms
   * float gyro_x
   * float gyro_y
   * float gyro_z */
  void gyroscope(ByteBuffer byteBuffer);
}
