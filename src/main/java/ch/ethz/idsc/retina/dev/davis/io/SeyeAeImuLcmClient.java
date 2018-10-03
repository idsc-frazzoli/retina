// code by az and jph
package ch.ethz.idsc.retina.dev.davis.io;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.retina.dev.davis.Aedat31Imu6Listener;

public class SeyeAeImuLcmClient extends SeyeAbstractLcmClient {
  private static final int AEDAT31IMU6EVENT_BYTES = 36;
  // ---
  public final List<Aedat31Imu6Listener> aedat31Imu6Listeners = new CopyOnWriteArrayList<>();

  public SeyeAeImuLcmClient(String channel) {
    super(channel);
  }

  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    byteBuffer.getShort(); // TODO CCODE
    int events = byteBuffer.remaining() / AEDAT31IMU6EVENT_BYTES; // TODO CCODE
    for (int count = 0; count < events; ++count) {
      Aedat31Imu6Event aedat31ImuEvent = new Aedat31Imu6Event(byteBuffer);
      aedat31Imu6Listeners.forEach(listener -> listener.imu6Event(aedat31ImuEvent));
    }
  }

  @Override
  protected String type() {
    return "aeimu";
  }
}