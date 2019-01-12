// code by jph
package ch.ethz.idsc.retina.davis.io;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

// TODO add units
public class Aedat31Imu6Event {
  private final int valid;
  private final int time;
  /** Acceleration in the X axis, measured in g == 9.81[m*s^-2] */
  private final float accel_x;
  private final float accel_y;
  private final float accel_z;
  /** Rotation in the X axis, measured in [deg*s^-1] */
  private final float gyro_x;
  private final float gyro_y;
  private final float gyro_z;
  /** Temperature, measured in [degC] */
  private final float temperature;

  public Aedat31Imu6Event(ByteBuffer byteBuffer) {
    final int value = byteBuffer.getInt(); // 4
    valid = value & 1;
    time = byteBuffer.getInt(); // 8
    accel_x = byteBuffer.getFloat(); // 12
    accel_y = byteBuffer.getFloat(); // 16
    accel_z = byteBuffer.getFloat(); // 20
    gyro_x = byteBuffer.getFloat(); // 24
    gyro_y = byteBuffer.getFloat(); // 28
    gyro_z = byteBuffer.getFloat(); // 32
    temperature = byteBuffer.getFloat(); // 36
  }

  public boolean isValid() {
    return valid == 1;
  }

  public int getTime_us() {
    return time;
  }

  public Tensor getAccel() {
    return Tensors.vector(accel_x, accel_y, accel_z);
  }

  public Tensor getGyro() {
    return Tensors.vector(gyro_x, gyro_y, gyro_z);
  }

  public Scalar getTemperature() {
    return Quantity.of(temperature, NonSI.DEGREE_CELSIUS);
  }
}
