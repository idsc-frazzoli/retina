// code by jph
package ch.ethz.idsc.gokart.lcm.imu;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.EnumSet;

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
public abstract class Vmu93xLcmServerBase extends AbstractModule implements Vmu931Listener {
  public static final Vmu931_G VMU931_G = Vmu931_G._16;
  // ---
  private final ByteArrayConsumer byteArrayConsumer;
  /** array for timestamp_ms accXYZ gyroXYZ */
  private final byte[] data = new byte[4 + 12 + 12];
  private final ByteBuffer byteBuffer = ByteBuffer.wrap(data);
  private final Vmu931 vmu931;

  public Vmu93xLcmServerBase(String port, String channel) {
    vmu931 = new Vmu931(port, //
        EnumSet.of(Vmu931Channel.ACCELEROMETER, Vmu931Channel.GYROSCOPE), //
        Vmu931_DPS._250, VMU931_G, this);
    byteArrayConsumer = new BinaryBlobPublisher(channel);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
  }

  @Override // from AbstractModule
  protected final void first() {
    vmu931.open();
  }

  @Override // from AbstractModule
  protected final void last() {
    vmu931.close();
  }

  @Override // from Vmu931Listener
  public final void accelerometer(ByteBuffer recvBuffer) {
    byteBuffer.position(0);
    byteBuffer.putInt(recvBuffer.getInt()); // timestamp_ms
    byteBuffer.putFloat(recvBuffer.getFloat()); // x
    byteBuffer.putFloat(recvBuffer.getFloat()); // y
    byteBuffer.putFloat(recvBuffer.getFloat()); // z
  }

  @Override // from Vmu931Listener
  public final void gyroscope(ByteBuffer recvBuffer) {
    recvBuffer.getInt(); // drop timestamp
    byteBuffer.position(16);
    byteBuffer.putFloat(recvBuffer.getFloat()); // x
    byteBuffer.putFloat(recvBuffer.getFloat()); // y
    byteBuffer.putFloat(recvBuffer.getFloat()); // z
    // ---
    byteArrayConsumer.accept(data); // publish
  }

  public final void requestStatus() {
    vmu931.requestStatus();
  }

  public final void requestCalibration() {
    vmu931.requestCalibration();
  }

  public final void requestSelftest() {
    vmu931.requestSelftest();
  }

  /** @return */
  public final boolean isCalibrated() {
    return vmu931.isCalibrated();
  }
}
