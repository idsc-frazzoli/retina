// code by jph
package ch.ethz.idsc.gokart.lcm.imu;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.EnumSet;
import java.util.Objects;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931Channel;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931Listener;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931_DPS;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931_G;
import ch.ethz.idsc.retina.util.io.ByteArrayConsumer;
import ch.ethz.idsc.retina.util.sys.AbstractModule;

public class Vmu931LcmServerModule extends AbstractModule implements Vmu931Listener {
  private static final String PORT = "/dev/ttyACM0";
  // ---
  private final ByteArrayConsumer byteArrayConsumer = new BinaryBlobPublisher(GokartLcmChannel.VMU931_AG);
  private final byte[] data = new byte[4 + 12 + 12];
  private final ByteBuffer send = ByteBuffer.wrap(data);
  private Vmu931 vmu931;

  @Override // from AbstractModule
  protected void first() throws Exception {
    send.order(ByteOrder.LITTLE_ENDIAN);
    vmu931 = new Vmu931(PORT, //
        EnumSet.of(Vmu931Channel.ACCELEROMETER, Vmu931Channel.GYROSCOPE), //
        Vmu931_DPS._250, Vmu931_G._16, this);
    System.out.println("first() leave");
  }

  @Override // from AbstractModule
  protected void last() {
    if (Objects.nonNull(vmu931))
      vmu931.close();
    System.out.println("last() leave");
  }

  @Override // from Vmu931Listener
  public void accelerometer(ByteBuffer recvBuffer) {
    send.position(0);
    send.putInt(recvBuffer.getInt()); // timestamp_ms
    send.putFloat(recvBuffer.getFloat()); // x
    send.putFloat(recvBuffer.getFloat()); // y
    send.putFloat(recvBuffer.getFloat()); // z
  }

  @Override // from Vmu931Listener
  public void gyroscope(ByteBuffer recvBuffer) {
    recvBuffer.getInt(); // drop timestamp
    send.position(16);
    send.putFloat(recvBuffer.getFloat()); // x
    send.putFloat(recvBuffer.getFloat()); // y
    send.putFloat(recvBuffer.getFloat()); // z
    byteArrayConsumer.accept(data);
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
}
