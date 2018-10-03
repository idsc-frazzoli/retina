// code by az and jph
package ch.ethz.idsc.retina.dev.davis.io;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.davis.Aedat31PolarityListener;

public class SeyeAeDvsLcmClient extends SeyeAbstractLcmClient {
  public static final int EVENT_BYTES = 8;
  // ---
  public final List<Aedat31PolarityListener> aedat31PolarityListeners = new LinkedList<>();

  public SeyeAeDvsLcmClient(String channel) {
    super(channel);
  }

  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    int events = byteBuffer.remaining() / EVENT_BYTES;
    for (int count = 0; count < events; ++count) {
      Aedat31PolarityEvent aedat31PolarityEvent = Aedat31PolarityEvent.create(byteBuffer);
      aedat31PolarityListeners.forEach(listener -> listener.polarityEvent(aedat31PolarityEvent));
    }
  }

  @Override
  protected String type() {
    return "aedvs";
  }
}
