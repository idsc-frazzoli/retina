// code by jph
package ch.ethz.idsc.retina.dev.davis.data;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.zhkart.DataEvent;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/** units are SI except for time stamp from chip */
public class DavisImuFrame extends DataEvent {
  /* package */ static final int LENGTH = 4 + 2 * 7;
  // ---
  private static final double TEMPERATURE_SCALE = 1.0 / 340;
  private static final double TEMPERATURE_OFFSET = 35.0;
  private static final double G_TO_M_S2 = 9.81;
  private static final Scalar M_S2PerLsb = Quantity.of(G_TO_M_S2 * 2.0 / 8192, "m*s^-2");
  private static final double DEG_TO_RAD = Math.PI / 180.0;
  private static final Scalar RadPerSecPerLsb = Quantity.of(DEG_TO_RAD * 2.0 / 65.5, "s^-1");
  // ---
  /** us == micro seconds */
  public final int time;
  /** acceleration in m/s^2, temperature, gyro */
  private final short accelX;
  private final short accelY;
  private final short accelZ;
  /** temperature in degree Celsius */
  private final short temperature;
  /** radians per seconds */
  private final short gyroX;
  private final short gyroY;
  private final short gyroZ;

  public DavisImuFrame(ByteBuffer byteBuffer) {
    time = byteBuffer.getInt();
    // ---
    accelX = byteBuffer.getShort();
    accelY = byteBuffer.getShort();
    accelZ = byteBuffer.getShort();
    temperature = byteBuffer.getShort();
    gyroX = byteBuffer.getShort();
    gyroY = byteBuffer.getShort();
    gyroZ = byteBuffer.getShort();
  }

  @Override
  protected int length() {
    return LENGTH;
  }

  @Override
  protected void insert(ByteBuffer byteBuffer) {
    byteBuffer.putInt(time);
    byteBuffer.putShort(accelX);
    byteBuffer.putShort(accelY);
    byteBuffer.putShort(accelZ);
    byteBuffer.putShort(temperature);
    byteBuffer.putShort(gyroX);
    byteBuffer.putShort(gyroY);
    byteBuffer.putShort(gyroZ);
  }

  // EXPERIMENTAL API not finalized
  public Tensor accel() {
    return Tensors.vector(accelX, -accelY, accelZ).multiply(M_S2PerLsb);
  }

  public Tensor gyro() {
    return Tensors.vector(-gyroX, gyroY, -gyroZ).multiply(RadPerSecPerLsb);
  }

  /** @return temperature in degC */
  public Scalar temperature() {
    return Quantity.of(temperature * TEMPERATURE_SCALE + TEMPERATURE_OFFSET, "degC");
  }
}
