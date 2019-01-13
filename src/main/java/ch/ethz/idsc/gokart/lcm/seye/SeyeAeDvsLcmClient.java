// code by az and jph
package ch.ethz.idsc.gokart.lcm.seye;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.gokart.lcm.davis.DvsLcmClient;
import ch.ethz.idsc.retina.davis.DavisDvsListener;
import ch.ethz.idsc.retina.davis.io.Aedat31PolarityEvent;

public class SeyeAeDvsLcmClient extends SeyeAbstractLcmClient implements DvsLcmClient {
  private static final int AEDAT31POLARITYEVENT_BYTES = 8;
  // ---
  private final List<DavisDvsListener> aedat31PolarityListeners = new CopyOnWriteArrayList<>();

  public SeyeAeDvsLcmClient(String channel) {
    super(channel, "aedvs");
  }

  @Override // from BinaryLcmClient
  public void messageReceived(ByteBuffer byteBuffer) {
    byteBuffer.getShort(); // packet id
    int events = byteBuffer.remaining() / AEDAT31POLARITYEVENT_BYTES;
    for (int count = 0; count < events; ++count) {
      Aedat31PolarityEvent aedat31PolarityEvent = Aedat31PolarityEvent.create(byteBuffer);
      aedat31PolarityListeners.forEach(listener -> listener.davisDvs(aedat31PolarityEvent));
    }
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
