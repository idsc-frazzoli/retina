// code by jph
package ch.ethz.idsc.retina.dev.davis.io;

import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ch.ethz.idsc.retina.dev.davis.Aedat31FrameListener;
import ch.ethz.idsc.retina.dev.davis.Aedat31PolarityListener;
import ch.ethz.idsc.retina.util.StartAndStoppable;

/** parser for aedat version 3.1 */
public class Aedat31FileSupplier implements StartAndStoppable {
  private static final int BUFFER_SIZE = 1689960;
  // ---
  private final byte[] head = new byte[28];
  private final byte[] data = new byte[BUFFER_SIZE];
  private final ByteBuffer headBuffer = ByteBuffer.wrap(head);
  private final ByteBuffer dataBuffer = ByteBuffer.wrap(data);
  private final Aedat31Decoder aedat31Decoder;
  private final InputStream inputStream;
  public final List<Aedat31PolarityListener> dvsPolarityListeners = new LinkedList<>();
  public final List<Aedat31FrameListener> dvsFrameListeners = new LinkedList<>();

  public Aedat31FileSupplier(File file, Aedat31Decoder aedat31Decoder) throws Exception {
    this.aedat31Decoder = aedat31Decoder;
    AedatFileHeader aedatFileHeader = new AedatFileHeader(file);
    inputStream = aedatFileHeader.getInputStream();
    headBuffer.order(ByteOrder.LITTLE_ENDIAN); // order defined by aedat format
    dataBuffer.order(ByteOrder.LITTLE_ENDIAN); // order defined by aedat format
  }

  public final Map<Integer, Integer> map = new TreeMap<>();
  int count = 0;

  @Override
  public void start() {
    try {
      while (true) {
        int available = inputStream.read(head, 0, head.length);
        if (available < 0)
          break;
        headBuffer.position(0);
        Aedat31EventHeader aedat31EventHeader = new Aedat31EventHeader(headBuffer);
        // System.out.println(aedat31EventHeader.getType());
        // if (!aedat31EventHeader.getType().equals(Aedat31EventType.FRAME_EVENT))
        // aedat31EventHeader.printInfoLine();
        // By multiplying eventCapacity with eventSize, and adding the 28 bytes of header size,
        // you can quickly and precisely calculate the total size of an event packet.
        // ---
        int size = aedat31EventHeader.getSize();
        // System.out.println(size);
        inputStream.read(data, 0, size);
        dataBuffer.position(0);
        switch (aedat31EventHeader.getType()) {
        case POLARITY_EVENT: {
          for (int count = 0; count < aedat31EventHeader.getNumber(); ++count) {
            Aedat31PolarityEvent aedat31PolarityEvent = new Aedat31PolarityEvent(dataBuffer);
            // aedat31Decoder.polarityEvent(aedat31PolarityEvent);
            dvsPolarityListeners.forEach(l -> l.polarityEvent(aedat31PolarityEvent));
          }
          break;
        }
        case FRAME_EVENT:
          Aedat31FrameEvent aedat31FrameEvent = new Aedat31FrameEvent(dataBuffer);
          dvsFrameListeners.forEach(l -> l.frameEvent(aedat31FrameEvent));
          break;
        default:
          break;
        }
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Override
  public void stop() {
    try {
      inputStream.close();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
