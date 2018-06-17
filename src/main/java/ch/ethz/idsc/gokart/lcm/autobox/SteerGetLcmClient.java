// code by jph
package ch.ethz.idsc.gokart.lcm.autobox;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.steer.SteerGetEvent;
import ch.ethz.idsc.retina.dev.steer.SteerGetListener;
import ch.ethz.idsc.retina.lcm.SimpleLcmClient;

public class SteerGetLcmClient extends SimpleLcmClient<SteerGetListener> {
  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    SteerGetEvent event = new SteerGetEvent(byteBuffer);
    listeners.forEach(listener -> listener.getEvent(event));
  }

  @Override // from BinaryLcmClient
  protected String channel() {
    return SteerLcmServer.CHANNEL_GET;
  }
}
