// code by jph
package ch.ethz.idsc.gokart.lcm.autobox;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutListener;
import ch.ethz.idsc.gokart.lcm.SimpleLcmClient;

public class RimoPutLcmClient extends SimpleLcmClient<RimoPutListener> {
  public RimoPutLcmClient() {
    super(RimoLcmServer.CHANNEL_PUT);
  }

  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    RimoPutEvent event = RimoPutHelper.from(byteBuffer);
    listeners.forEach(listener -> listener.putEvent(event));
  }
}
