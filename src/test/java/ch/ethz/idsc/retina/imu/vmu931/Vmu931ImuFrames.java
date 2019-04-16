// code by jph
package ch.ethz.idsc.retina.imu.vmu931;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;

/** FOR USE IN TESTS ONLY */
public enum Vmu931ImuFrames {
  ;
  public static Vmu931ImuFrame create(int timestamp_ms, Tensor acc, Tensor gyro) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[4 * 7]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putInt(timestamp_ms);
    byteBuffer.putFloat(Magnitude.ACCELERATION.toFloat(acc.Get(0)));
    byteBuffer.putFloat(Magnitude.ACCELERATION.toFloat(acc.Get(1)));
    byteBuffer.putFloat(Magnitude.ACCELERATION.toFloat(acc.Get(2)));
    byteBuffer.putFloat(Magnitude.PER_SECOND.toFloat(gyro.Get(0)));
    byteBuffer.putFloat(Magnitude.PER_SECOND.toFloat(gyro.Get(1)));
    byteBuffer.putFloat(Magnitude.PER_SECOND.toFloat(gyro.Get(2)));
    byteBuffer.position(0);
    return new Vmu931ImuFrame(byteBuffer);
  }
}
