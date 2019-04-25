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
    byteBuffer.putFloat((float) (Magnitude.ACCELERATION.toDouble(acc.Get(0)) / Vmu931ImuFrame.G_TO_M_S2));
    byteBuffer.putFloat((float) (Magnitude.ACCELERATION.toDouble(acc.Get(1)) / Vmu931ImuFrame.G_TO_M_S2));
    byteBuffer.putFloat((float) (Magnitude.ACCELERATION.toDouble(acc.Get(2)) / Vmu931ImuFrame.G_TO_M_S2));
    byteBuffer.putFloat((float) (Magnitude.PER_SECOND.toDouble(gyro.Get(0)) / Vmu931ImuFrame.DPS_TO_RPS));
    byteBuffer.putFloat((float) (Magnitude.PER_SECOND.toDouble(gyro.Get(1)) / Vmu931ImuFrame.DPS_TO_RPS));
    byteBuffer.putFloat((float) (Magnitude.PER_SECOND.toDouble(gyro.Get(2)) / Vmu931ImuFrame.DPS_TO_RPS));
    byteBuffer.flip();
    return new Vmu931ImuFrame(byteBuffer);
  }
}
