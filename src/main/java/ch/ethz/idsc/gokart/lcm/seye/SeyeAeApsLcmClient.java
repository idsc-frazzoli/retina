// code by az and jph
package ch.ethz.idsc.gokart.lcm.seye;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.retina.davis.Aedat31FrameListener;

public class SeyeAeApsLcmClient extends SeyeAbstractLcmClient {
  public final List<Aedat31FrameListener> aedat31FrameListeners = new CopyOnWriteArrayList<>();
  int count = 0;

  public SeyeAeApsLcmClient(String channel) {
    super(channel, "aeaps");
  }

  @Override
  protected void messageReceived(ByteBuffer byteBuffer) {
    BufferedImage d = new BufferedImage(320, 264, BufferedImage.TYPE_BYTE_GRAY);
    // ImageForma/t
    System.out.println("frame re");
    // int events = byteBuffer.remaining() / 40;
    // for (int count = 0; count < events; ++count) {
    // Aedat31FrameEvent aedat31FrameEvent = new Aedat31FrameEvent(byteBuffer);
    // aedat31FrameListeners.forEach(listener -> listener.frameEvent(aedat31FrameEvent));
    // }
  }
}
