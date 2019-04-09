// code by jph
package ch.ethz.idsc.gokart.lcm.imu;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.EnumSet;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931Channel;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931Listener;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931_DPS;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931_G;
import ch.ethz.idsc.retina.util.io.ByteArrayConsumer;
import ch.ethz.idsc.retina.util.sys.AbstractModule;

/** configures VMU931 IMU to 1000Hz readout
 * listens to acc and gyro messages
 * publishes acc and gyro data via lcm */
public class Vmu931LcmServerModule extends AbstractModule implements Vmu931Listener {
  private static final String PORT = "/dev/ttyACM0";
  // ---
  private final ByteArrayConsumer byteArrayConsumer = new BinaryBlobPublisher(GokartLcmChannel.VMU931_AG);
  /** array for timestamp_ms accXYZ gyroXYZ */
  private final byte[] data = new byte[4 + 12 + 12];
  private final ByteBuffer byteBuffer = ByteBuffer.wrap(data);
  private final Vmu931 vmu931 = new Vmu931(PORT, //
      EnumSet.of(Vmu931Channel.ACCELEROMETER, Vmu931Channel.GYROSCOPE), //
      Vmu931_DPS._250, Vmu931_G._16, this);

  public Vmu931LcmServerModule() {
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
  }

  @Override // from AbstractModule
  protected void first() {
    vmu931.open();
  }

  @Override // from AbstractModule
  protected void last() {
    vmu931.close();
  }

  @Override // from Vmu931Listener
  public void accelerometer(ByteBuffer recvBuffer) {
    byteBuffer.position(0);
    byteBuffer.putInt(recvBuffer.getInt()); // timestamp_ms
    byteBuffer.putFloat(recvBuffer.getFloat()); // x
    byteBuffer.putFloat(recvBuffer.getFloat()); // y
    byteBuffer.putFloat(recvBuffer.getFloat()); // z
  }

  @Override // from Vmu931Listener
  public void gyroscope(ByteBuffer recvBuffer) {
    recvBuffer.getInt(); // drop timestamp
    byteBuffer.position(16);
    byteBuffer.putFloat(recvBuffer.getFloat()); // x
    byteBuffer.putFloat(recvBuffer.getFloat()); // y
    byteBuffer.putFloat(recvBuffer.getFloat()); // z
    // ---
    byteArrayConsumer.accept(data); // publish
  }

  public void requestStatus() {
    vmu931.requestStatus();
  }

  public void requestCalibration() {
    vmu931.requestCalibration();
  }

  public void requestSelftest() {
    vmu931.requestSelftest();
  }

  /** @return */
  public boolean isCalibrated() {
    return vmu931.isCalibrated();
  }
}
