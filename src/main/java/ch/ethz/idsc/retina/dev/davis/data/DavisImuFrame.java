// code by jph
package ch.ethz.idsc.retina.dev.davis.data;

import java.nio.ByteBuffer;

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

  @Deprecated
  public int length() {
    return 4 + 7 * 4;
  }

  @Deprecated
  public void get(ByteBuffer byteBuffer) {
    byteBuffer.putInt(time);
    byteBuffer.putFloat(accelX);
    byteBuffer.putFloat(accelY);
    byteBuffer.putFloat(accelZ);
    byteBuffer.putFloat(temperature);
    byteBuffer.putFloat(gyroX);
    byteBuffer.putFloat(gyroY);
    byteBuffer.putFloat(gyroZ);
  }

  @Deprecated
  public void print() {
    System.out.println(String.format("accelX %f", accelX));
    System.out.println(String.format("accelY %f", accelY));
    System.out.println(String.format("accelZ %f", accelZ));
    System.out.println(String.format("temp.  %f", temperature));
    System.out.println(String.format("gyroX  %f", gyroX));
    System.out.println(String.format("gyroY  %f", gyroY));
    System.out.println(String.format("gyroZ  %f", gyroZ));
  }
}
