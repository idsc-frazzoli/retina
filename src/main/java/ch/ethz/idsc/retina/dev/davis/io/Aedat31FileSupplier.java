// code by jph
package ch.ethz.idsc.retina.dev.davis.io;

import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.dev.davis.DavisDecoder;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.tensor.io.AnimatedGifWriter;

/** parser for aedat version 3.1 */
public class Aedat31FileSupplier implements StartAndStoppable {
  private static final int BUFFER_SIZE = 1689960;
  // ---
  private final byte[] head = new byte[28];
  private final byte[] data = new byte[BUFFER_SIZE];
  private final ByteBuffer headBuffer = ByteBuffer.wrap(head);
  private final ByteBuffer dataBuffer = ByteBuffer.wrap(data);
  private final DavisDecoder davisDecoder;
  private final InputStream inputStream;

  public Aedat31FileSupplier(File file, DavisDecoder davisDecoder) throws Exception {
    this.davisDecoder = davisDecoder;
    AedatFileHeader aedatFileHeader = new AedatFileHeader(file);
    inputStream = aedatFileHeader.getInputStream();
    headBuffer.order(ByteOrder.LITTLE_ENDIAN); // order defined by aedat format
    dataBuffer.order(ByteOrder.LITTLE_ENDIAN); // order defined by aedat format
  }

  @Override
  public void start() {
    try {
      AnimatedGifWriter agw = AnimatedGifWriter.of(UserHome.file("some.gif"), 100);
      while (true) {
        int available = inputStream.read(head, 0, head.length);
        if (available < 0)
          break;
        headBuffer.position(0);
        Aedat31EventHeader aedat31EventHeader = new Aedat31EventHeader(headBuffer);
        if (aedat31EventHeader.getType().equals(Aedat31EventType.FRAME_EVENT))
          aedat31EventHeader.printInfoLine();
        // By multiplying eventCapacity with eventSize, and adding the 28 bytes of header size,
        // you can quickly and precisely calculate the total size of an event packet.
        // ---
        int size = aedat31EventHeader.getSize();
        // System.out.println(size);
        inputStream.read(data, 0, size);
        dataBuffer.position(0);
        if (aedat31EventHeader.getType().equals(Aedat31EventType.FRAME_EVENT)) {
          Aedat31FrameEvent aedat31FrameEvent = new Aedat31FrameEvent(dataBuffer);
          aedat31FrameEvent.printInfoLine();
          agw.append(aedat31FrameEvent.getBufferedImage());
        }
        // inputStream.read(bytes, 0, bytes.length);
        // if (available == 0) {
        // short asd = dataInputStream.readShort();
        // System.out.println(asd);
        // available += inputStream.read(bytes, 0, bytes.length);
        // if (available < 2) // end of file, at least 2 bytes are required for next decoding
        // break;
        // }
        // davisDecoder.read(headBuffer);
        // available -= 8;
        // break;
      }
      agw.close();
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
