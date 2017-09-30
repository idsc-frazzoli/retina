// code by jph
package ch.ethz.idsc.retina.lcm.autobox;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;

public class LinmotGetLcmClient extends SimpleLcmClient<LinmotGetListener> {
  @Override
  protected void digest(ByteBuffer byteBuffer) {
    LinmotGetEvent linmotGetEvent = new LinmotGetEvent(byteBuffer);
    listeners.forEach(listener -> listener.getEvent(linmotGetEvent));
  }

  @Override
  protected String name() {
    return LinmotLcmServer.CHANNEL_GET;
  }
}
