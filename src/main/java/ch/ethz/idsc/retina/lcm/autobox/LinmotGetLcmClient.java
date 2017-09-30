// code by jph
package ch.ethz.idsc.retina.lcm.autobox;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;

public class LinmotGetLcmClient extends SimpleLcmClient<LinmotGetListener> {
  public LinmotGetLcmClient() {
    super(LinmotLcmServer.CHANNEL_GET);
  }

  @Override
  protected void createEvent(ByteBuffer byteBuffer) {
    LinmotGetEvent linmotGetEvent = new LinmotGetEvent(byteBuffer);
    listeners.forEach(listener -> listener.getEvent(linmotGetEvent));
  }
}
