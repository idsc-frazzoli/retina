// code by jph
package ch.ethz.idsc.gokart.lcm.autobox;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.retina.lcm.SimpleLcmClient;

public class LinmotGetLcmClient extends SimpleLcmClient<LinmotGetListener> {
  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    LinmotGetEvent linmotGetEvent = new LinmotGetEvent(byteBuffer);
    listeners.forEach(listener -> listener.getEvent(linmotGetEvent));
  }

  @Override // from BinaryLcmClient
  protected String channel() {
    return LinmotLcmServer.CHANNEL_GET;
  }
}
