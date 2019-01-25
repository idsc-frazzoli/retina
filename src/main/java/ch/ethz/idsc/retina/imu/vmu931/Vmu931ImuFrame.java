// code by jph
package ch.ethz.idsc.retina.imu.vmu931;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

public class Vmu931ImuFrame {
  private static final double DPS_TO_RPS = Magnitude.PER_SECOND.toDouble(Quantity.of(1, "deg*s^-1"));
  // TODO redundant DavisImuFrame
  private static final double G_TO_M_S2 = 9.81;
  // ---
  /** milli seconds */
  private final int timestamp_ms;
  /** g */
  private final float acc_x;
  private final float acc_y;
  private final float acc_z;
  /** dps == deg*s^-1 */
  private final float gyro_x;
  private final float gyro_y;
  private final float gyro_z;

  public Vmu931ImuFrame(ByteBuffer byteBuffer) {
    timestamp_ms = byteBuffer.getInt();
    acc_x = byteBuffer.getFloat();
    acc_y = byteBuffer.getFloat();
    acc_z = byteBuffer.getFloat();
    gyro_x = byteBuffer.getFloat();
    gyro_y = byteBuffer.getFloat();
    gyro_z = byteBuffer.getFloat();
  }

  public int timestamp_ms() {
    return timestamp_ms;
  }

  public Tensor acceleration() {
    return Tensors.of( //
        Quantity.of(acc_x * G_TO_M_S2, SI.ACCELERATION), //
        Quantity.of(acc_y * G_TO_M_S2, SI.ACCELERATION), //
        Quantity.of(acc_z * G_TO_M_S2, SI.ACCELERATION));
  }

  public Tensor accXY() {
    return Tensors.of( //
        Quantity.of(acc_x * G_TO_M_S2, SI.ACCELERATION), //
        Quantity.of(acc_y * G_TO_M_S2, SI.ACCELERATION));
  }

  public Tensor gyroscope() {
    return Tensors.of( //
        Quantity.of(gyro_x * DPS_TO_RPS, SI.PER_SECOND), //
        Quantity.of(gyro_y * DPS_TO_RPS, SI.PER_SECOND), //
        Quantity.of(gyro_z * DPS_TO_RPS, SI.PER_SECOND));
  }

  public Tensor gyroZ() {
    return Quantity.of(gyro_z * DPS_TO_RPS, SI.PER_SECOND);
  }
}
