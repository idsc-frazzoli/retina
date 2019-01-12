// code by jph
package ch.ethz.idsc.retina.davis.io;

import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.davis.Aedat31FrameListener;
import ch.ethz.idsc.retina.davis.Aedat31Imu6Listener;
import ch.ethz.idsc.retina.davis.DavisDvsListener;
import ch.ethz.idsc.retina.util.StartAndStoppable;

/** parser for aedat version 3.1 */
// TODO not final class design!
public class Aedat31FileSupplier implements StartAndStoppable {
  private static final int BUFFER_SIZE = 168996 * 2;
  // ---
  private final byte[] head = new byte[28];
  private final byte[] data = new byte[BUFFER_SIZE];
  private final ByteBuffer headBuffer = ByteBuffer.wrap(head);
  private final ByteBuffer dataBuffer = ByteBuffer.wrap(data);
  private final InputStream inputStream;
  public final List<DavisDvsListener> aedat31PolarityListeners = new LinkedList<>();
  public final List<Aedat31FrameListener> aedat31FrameListeners = new LinkedList<>();
  public final List<Aedat31Imu6Listener> aedat31Imu6Listeners = new LinkedList<>();

  public Aedat31FileSupplier(File file) throws Exception {
    AedatFileHeader aedatFileHeader = new AedatFileHeader(file);
    inputStream = aedatFileHeader.getInputStream();
    headBuffer.order(ByteOrder.LITTLE_ENDIAN); // order defined by aedat format
    dataBuffer.order(ByteOrder.LITTLE_ENDIAN); // order defined by aedat format
  }

  @Override
  public void start() {
    try {
      while (true) {
        int available = inputStream.read(head, 0, head.length);
        if (available < 0)
          break;
        headBuffer.position(0);
        Aedat31EventHeader aedat31EventHeader = new Aedat31EventHeader(headBuffer);
        // ---
        int size = aedat31EventHeader.getSize();
        inputStream.read(data, 0, size);
        dataBuffer.position(0);
        switch (aedat31EventHeader.getType()) {
        case POLARITY_EVENT: {
          for (int count = 0; count < aedat31EventHeader.getNumber(); ++count) {
            Aedat31PolarityEvent aedat31PolarityEvent = Aedat31PolarityEvent.create(dataBuffer);
            aedat31PolarityListeners.forEach(listener -> listener.davisDvs(aedat31PolarityEvent));
          }
          break;
        }
        case FRAME_EVENT:
          Aedat31FrameEvent aedat31FrameEvent = new Aedat31FrameEvent(dataBuffer);
          aedat31FrameListeners.forEach(listener -> listener.frameEvent(aedat31FrameEvent));
          break;
        case IMU6_EVENT:
          Aedat31Imu6Event aedat31Imu6Event = new Aedat31Imu6Event(dataBuffer);
          aedat31Imu6Listeners.forEach(listener -> listener.imu6Event(aedat31Imu6Event));
          break;
        default:
          System.err.println("unprocessed: " + aedat31EventHeader.getType());
          aedat31EventHeader.printInfoLine();
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
