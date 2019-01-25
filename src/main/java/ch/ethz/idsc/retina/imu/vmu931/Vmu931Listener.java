// code by jph
package ch.ethz.idsc.retina.imu.vmu931;

import java.nio.ByteBuffer;

public interface Vmu931Listener {
  void accelerometer(ByteBuffer byteBuffer);

  void gyroscope(ByteBuffer byteBuffer);
}
