// code by jph
package ch.ethz.idsc.retina.lcm.autobox;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.retina.dev.rimo.RimoPutListener;

public class RimoPutLcmClient extends SimpleLcmClient<RimoPutListener> {
  @Override
  protected void messageReceived(ByteBuffer byteBuffer) {
    RimoPutEvent event = RimoPutHelper.from(byteBuffer);
    listeners.forEach(listener -> listener.putEvent(event));
  }

  @Override
  protected String channel() {
    return RimoLcmServer.CHANNEL_PUT;
  }
}
