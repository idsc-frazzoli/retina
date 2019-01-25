// code by jph
package ch.ethz.idsc.gokart.lcm.imu;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

public class Vmu931Frame {
  private static final double DPS_TO_RPS = Magnitude.PER_SECOND.toDouble(Quantity.of(1, "deg*s^-1"));
  private static final double G = 9.81;
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

  public Vmu931Frame(ByteBuffer byteBuffer) {
    timestamp_ms = byteBuffer.getInt();
    acc_x = byteBuffer.getFloat();
    acc_y = byteBuffer.getFloat();
    acc_z = byteBuffer.getFloat();
    gyro_x = byteBuffer.getFloat();
    gyro_y = byteBuffer.getFloat();
    gyro_z = byteBuffer.getFloat();
  }

  public Tensor acceleration() {
    return Tensors.of( //
        Quantity.of(acc_x * G, SI.ACCELERATION), //
        Quantity.of(acc_y * G, SI.ACCELERATION), //
        Quantity.of(acc_z * G, SI.ACCELERATION) //
    );
  }

  public Tensor gyroscope() {
    return Tensors.of( //
        Quantity.of(gyro_x * DPS_TO_RPS, SI.PER_SECOND), //
        Quantity.of(gyro_y * DPS_TO_RPS, SI.PER_SECOND), //
        Quantity.of(gyro_z * DPS_TO_RPS, SI.PER_SECOND) //
    );
  }

  public Tensor gyroZ() {
    return Quantity.of(gyro_z * DPS_TO_RPS, SI.PER_SECOND);
  }

  public static void main(String[] args) {
    // Scalar scalar = UnitConvert.SI().to(SI.PER_SECOND).apply(Quantity.of(1, "deg*s^-1"));
    Scalar scalar = Magnitude.PER_SECOND.apply(Quantity.of(1, "deg*s^-1"));
    System.out.println(scalar);
    double double1 = Magnitude.PER_SECOND.toDouble(Quantity.of(1, "deg*s^-1"));
    System.out.println(double1);
  }
}
