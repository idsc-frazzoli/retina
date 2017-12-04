// code by jph
package ch.ethz.idsc.retina.lcm.autobox;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.gui.gokart.GokartStatusEvent;
import ch.ethz.idsc.retina.gui.gokart.GokartStatusLcmModule;
import ch.ethz.idsc.retina.gui.gokart.GokartStatusListener;

public class GokartStatusLcmClient extends SimpleLcmClient<GokartStatusListener> {
  @Override // from BinaryLcmClient
  protected void digest(ByteBuffer byteBuffer) {
    GokartStatusEvent event = new GokartStatusEvent(byteBuffer);
    listeners.forEach(listener -> listener.getEvent(event));
  }

  @Override // from BinaryLcmClient
  protected String name() {
    return GokartStatusLcmModule.CHANNEL;
  }
}
