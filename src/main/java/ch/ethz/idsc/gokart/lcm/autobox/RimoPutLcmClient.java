// code by jph
package ch.ethz.idsc.gokart.lcm.autobox;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.retina.dev.rimo.RimoPutListener;
import ch.ethz.idsc.retina.lcm.SimpleLcmClient;

public class RimoPutLcmClient extends SimpleLcmClient<RimoPutListener> {
  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    RimoPutEvent event = RimoPutHelper.from(byteBuffer);
    listeners.forEach(listener -> listener.putEvent(event));
  }

  @Override // from BinaryLcmClient
  protected String channel() {
    return RimoLcmServer.CHANNEL_PUT;
  }
}
