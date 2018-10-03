// code by az and jph
package ch.ethz.idsc.retina.dev.davis.io;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.davis.Aedat31Imu6Listener;

public class SeyeAeImuLcmClient extends SeyeAbstractLcmClient {
  public final List<Aedat31Imu6Listener> aedat31Imu6Listeners = new LinkedList<>();

  public SeyeAeImuLcmClient(String channel) {
    super(channel);
  }

  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    int events = byteBuffer.remaining() / 40;
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
