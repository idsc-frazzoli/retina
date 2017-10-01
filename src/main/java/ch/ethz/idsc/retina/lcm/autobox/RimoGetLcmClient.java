// code by jph
package ch.ethz.idsc.retina.lcm.autobox;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;

public class RimoGetLcmClient extends SimpleLcmClient<RimoGetListener> {
  @Override
  protected void digest(ByteBuffer byteBuffer) {
    RimoGetEvent event = new RimoGetEvent(byteBuffer);
    listeners.forEach(listener -> listener.getEvent(event));
  }

  @Override
  protected String name() {
    return RimoLcmServer.CHANNEL_GET;
  }
}
