// code by jph
package ch.ethz.idsc.gokart.lcm.autobox;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.gokart.lcm.SimpleLcmClient;

public class LinmotGetLcmClient extends SimpleLcmClient<LinmotGetListener> {
  public LinmotGetLcmClient() {
    super(LinmotLcmServer.CHANNEL_GET);
  }

  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    LinmotGetEvent linmotGetEvent = new LinmotGetEvent(byteBuffer);
    listeners.forEach(listener -> listener.getEvent(linmotGetEvent));
  }
}
