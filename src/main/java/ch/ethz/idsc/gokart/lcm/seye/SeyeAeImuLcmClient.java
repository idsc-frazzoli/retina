// code by az and jph
package ch.ethz.idsc.gokart.lcm.seye;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.retina.davis.Aedat31Imu6Listener;
import ch.ethz.idsc.retina.davis.io.Aedat31Imu6Event;

public class SeyeAeImuLcmClient extends SeyeAbstractLcmClient {
  private static final int AEDAT31IMU6EVENT_BYTES = 36;
  // ---
  public final List<Aedat31Imu6Listener> aedat31Imu6Listeners = new CopyOnWriteArrayList<>();

  public SeyeAeImuLcmClient(String channel) {
    super(channel, "aeimu");
  }

  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    byteBuffer.getShort();
    int events = byteBuffer.remaining() / AEDAT31IMU6EVENT_BYTES;
    for (int count = 0; count < events; ++count) {
      Aedat31Imu6Event aedat31ImuEvent = new Aedat31Imu6Event(byteBuffer);
      aedat31Imu6Listeners.forEach(listener -> listener.imu6Event(aedat31ImuEvent));
    }
  }
}
