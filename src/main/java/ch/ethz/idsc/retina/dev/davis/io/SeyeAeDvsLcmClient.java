// code by az and jph
package ch.ethz.idsc.retina.dev.davis.io;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;

public class SeyeAeDvsLcmClient extends SeyeAbstractLcmClient {
  private static final int AEDAT31POLARITYEVENT_BYTES = 8;
  // ---
  public final List<DavisDvsListener> aedat31PolarityListeners = new CopyOnWriteArrayList<>();

  public SeyeAeDvsLcmClient(String channel) {
    super(channel);
  }

  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    byteBuffer.getShort(); // TODO CCODE
    int events = byteBuffer.remaining() / AEDAT31POLARITYEVENT_BYTES;
    for (int count = 0; count < events; ++count) {
      Aedat31PolarityEvent aedat31PolarityEvent = Aedat31PolarityEvent.create(byteBuffer);
      aedat31PolarityListeners.forEach(listener -> listener.davisDvs(aedat31PolarityEvent));
    }
  }

  @Override
  protected String type() {
    return "aedvs";
  }
}
