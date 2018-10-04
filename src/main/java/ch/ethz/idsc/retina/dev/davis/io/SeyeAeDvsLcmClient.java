// code by az and jph
package ch.ethz.idsc.retina.dev.davis.io;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;

public class SeyeAeDvsLcmClient extends SeyeAbstractLcmClient implements DvsLcmClient {
  private static final int AEDAT31POLARITYEVENT_BYTES = 8;
  // ---
  private final List<DavisDvsListener> aedat31PolarityListeners = new CopyOnWriteArrayList<>();

  public SeyeAeDvsLcmClient(String channel) {
    super(channel);
    System.out.println(channel());
  }

  @Override // from BinaryLcmClient
  public void messageReceived(ByteBuffer byteBuffer) {
    byteBuffer.getShort();
    // System.out.println("recv");
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

  @Override
  public void addDvsListener(DavisDvsListener davisDvsListener) {
    aedat31PolarityListeners.add(davisDvsListener);
  }

  @Override
  public void removeDvsListener(DavisDvsListener davisDvsListener) {
    aedat31PolarityListeners.remove(davisDvsListener);
  }
}
