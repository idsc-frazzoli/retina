// code by jph
package ch.ethz.idsc.retina.lcm.autobox;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.steer.SteerGetEvent;
import ch.ethz.idsc.retina.dev.steer.SteerGetListener;

public class SteerGetLcmClient extends SimpleLcmClient<SteerGetListener> {
  @Override
  protected void digest(ByteBuffer byteBuffer) {
    SteerGetEvent event = new SteerGetEvent(byteBuffer);
    listeners.forEach(listener -> listener.getEvent(event));
  }

  @Override
  protected String name() {
    return SteerLcmServer.CHANNEL_GET;
  }
}
