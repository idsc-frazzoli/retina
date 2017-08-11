// code by jph
package ch.ethz.idsc.retina.davis.io.aps;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.davis.ColumnTimedImageListener;
import ch.ethz.idsc.retina.davis.DavisDecoder;

// TODO lot's of magic const in this class
public class ApsDatagramClient {
  private static final int MAX_PACKET_SIZE = 2048; // TODO ensure that no server sends larger packets
  private final DavisDecoder davisDecoder;
  private final BufferedImage bufferedImage;
  private final int[] time = new int[240];
  private final byte[] imageData;
  private final List<ColumnTimedImageListener> listeners = new LinkedList<>();

  public ApsDatagramClient(DavisDecoder davisDecoder) {
    this.davisDecoder = davisDecoder;
    bufferedImage = new BufferedImage(240, 180, BufferedImage.TYPE_BYTE_GRAY);
    WritableRaster writableRaster = bufferedImage.getRaster();
    DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
    imageData = dataBufferByte.getData();
  }

  public void addListener(ColumnTimedImageListener columnTimedImageListener) {
    listeners.add(columnTimedImageListener);
  }

  // @Override
  public void start() {
    try (DatagramSocket datagramSocket = new DatagramSocket(ApsDatagramServer.PORT)) {
      byte[] bytes = new byte[MAX_PACKET_SIZE];
      ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
      byteBuffer.order(davisDecoder.getByteOrder());
      DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
      boolean isComplete = true;
      int x_next = 0;
      while (true) {
        datagramSocket.receive(datagramPacket);
        byteBuffer.position(0);
        int x = byteBuffer.getShort();
        isComplete &= x == x_next;
        for (int column = 0; column < 8; ++column) {
          time[x] = byteBuffer.getInt();
          for (int y = 0; y < 180; ++y)
            imageData[x + y * 240] = byteBuffer.get();
          ++x;
        }
        x_next = x;
        if (x == 240) {
          final boolean complete = isComplete;
          listeners.forEach(listener -> listener.image(time, bufferedImage, complete));
          isComplete = true;
          x_next = 0;
        }
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  // @Override
  public void stop() {
    // close socket
  }
}
