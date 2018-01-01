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
    return Tensors.vector(0, 0, 0);
  }

  public Tensor gyro() {
    return Tensors.vector(0, 0, 0);
  }

  public Scalar temperature() {
    return Quantity.of(22, "degC");
  }
}
