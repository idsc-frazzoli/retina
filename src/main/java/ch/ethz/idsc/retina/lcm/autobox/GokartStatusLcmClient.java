// code by jph
package ch.ethz.idsc.retina.lcm.autobox;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.gokart.gui.GokartStatusListener;

public class GokartStatusLcmClient extends SimpleLcmClient<GokartStatusListener> {
  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    GokartStatusEvent event = new GokartStatusEvent(byteBuffer);
    listeners.forEach(listener -> listener.getEvent(event));
  }

  @Override // from BinaryLcmClient
  protected String channel() {
    return GokartLcmChannel.STATUS;
  }
}
