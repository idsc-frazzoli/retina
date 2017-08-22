// code by jph
package ch.ethz.idsc.retina.dev.davis.data;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** units are SI except for time stamp from chip */
public class DavisImuFrame {
  /** us == micro seconds */
  public final int time;
  /** acceleration in m/s^2 */
  public final float accelX;
  public final float accelY;
  public final float accelZ;
  /** temperature in degree Celsius */
  public final float temperature;
  /** radians per seconds */
  public final float gyroX;
  public final float gyroY;
  public final float gyroZ;

  public DavisImuFrame(int clock_usec, float[] accel, float temperature, float[] gyro) {
    time = clock_usec;
    this.accelX = accel[0];
    this.accelY = accel[1];
    this.accelZ = accel[2];
    this.temperature = temperature;
    this.gyroX = gyro[0];
    this.gyroY = gyro[1];
    this.gyroZ = gyro[2];
  }

  public DavisImuFrame(int time, float[] values) {
    this.time = time;
    this.accelX = values[0];
    this.accelY = values[1];
    this.accelZ = values[2];
    this.temperature = values[3];
    this.gyroX = values[4];
    this.gyroY = values[5];
    this.gyroZ = values[6];
  }

  // EXPERIMENTAL API not finalized
  public Tensor accel() {
    return Tensors.vector(accelX, accelY, accelZ);
  }

  public Tensor gyro() {
    return Tensors.vector(gyroX, gyroY, gyroZ);
  }

  public Scalar temperature() {
    return RealScalar.of(temperature);
  }
}
