// code by jph
package ch.ethz.idsc.retina.davis.data;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.DataEvent;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/** experiments show that the timing of the Davis240C imu packets are
 * regular and correlate well with the transmission via lcm.
 * 
 * the image frame is defined as
 * 1st axis i.e. x is as pixel x direction: left to right
 * 2nd axis i.e. y is as pixel y direction: top to bottom
 * 3rd axis i.e. z is in direction of ccd array to lens to outside, i.e. forward */
public class DavisImuFrame extends DataEvent {
  /* package */ static final int LENGTH = 4 + 2 * 7;
  // ---
  // TODO these constants depend on the camera configuration and are only valid for the specific
  // choice of settings for the imu chip!
  private static final double TEMPERATURE_SCALE = 1.0 / 340;
  private static final double TEMPERATURE_OFFSET = 35.0;
  private static final double G_TO_M_S2 = 9.81;
  private static final Scalar M_S2PerLsb = Quantity.of(G_TO_M_S2 * 2.0 / 8192, SI.ACCELERATION);
  /** gyro rate matches angular rate derived from odometry in no-slip condition */
  private static final double DEG_TO_RAD = Math.PI / 180.0;
  private static final Scalar RadPerSecPerLsb = Quantity.of(DEG_TO_RAD * 2.0 / 65.5, SI.PER_SECOND);
  // ---
  /** us == micro seconds */
  private final int time;
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

  public int time_us_raw() {
    return time;
  }

  public Scalar getTime() {
    return Quantity.of(time, NonSI.MICRO_SECOND);
  }

  public Scalar getTimeRelativeTo(int time_us_zero) {
    return Quantity.of(time - time_us_zero, NonSI.MICRO_SECOND);
  }

  /** see above definition of image frame
   * 
   * Hint: the accelerometers in some cameras exhibit constant bias,
   * for instance in vertical position the z-component is centered
   * around 3[m*s^-2] instead of 0[m*s^-2].
   * 
   * @return */
  public Tensor accelImageFrame() {
    // DO NOT MODIFY BUT ADD NEW FUNCTION FOR DIFFERENT FRAME
    return Tensors.vector(accelX, -accelY, accelZ).multiply(M_S2PerLsb);
  }

  /** see above definition of image frame
   * 
   * @return */
  public Tensor gyroImageFrame() {
    // DO NOT MODIFY BUT ADD NEW FUNCTION FOR DIFFERENT FRAME
    return Tensors.vector(-gyroX, gyroY, -gyroZ).multiply(RadPerSecPerLsb);
  }

  /** function demos that other frames can be defined
   * 
   * @return */
  public Tensor accelRobotFrameFrontCamera() {
    // TODO not tested
    return Tensors.vector(accelZ, -accelX, accelY).multiply(M_S2PerLsb);
  }

  /** @return temperature in degC */
  public Scalar temperature() {
    return Quantity.of(temperature * TEMPERATURE_SCALE + TEMPERATURE_OFFSET, NonSI.DEGREE_CELSIUS);
  }

  @Override // from OfflineVectorInterface
  public Tensor asVector() {
    throw new RuntimeException();
  }
}
