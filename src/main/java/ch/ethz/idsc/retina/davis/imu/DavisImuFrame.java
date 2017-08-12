// code by jph
package ch.ethz.idsc.retina.davis.imu;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** in the current implementation the units are chosen to match the jAER demo
 * 
 * NOTICE: in the future, the units can be changed to SI */
public class DavisImuFrame {
  /** us == micro seconds */
  public final int time;
  /** acceleration in G */
  public final float accelX;
  public final float accelY;
  public final float accelZ;
  /** temperature in degree Celsius */
  public final float temperature;
  /** degree per seconds */
  public final float gyroX;
  public final float gyroY;
  public final float gyroZ;

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
