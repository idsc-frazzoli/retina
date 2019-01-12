// code by jph
package ch.ethz.idsc.gokart.lcm.autobox;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerGetListener;
import ch.ethz.idsc.gokart.lcm.SimpleLcmClient;

public class SteerGetLcmClient extends SimpleLcmClient<SteerGetListener> {
  public SteerGetLcmClient() {
    super(SteerLcmServer.CHANNEL_GET);
  }

  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    SteerGetEvent event = new SteerGetEvent(byteBuffer);
    listeners.forEach(listener -> listener.getEvent(event));
  }
}
