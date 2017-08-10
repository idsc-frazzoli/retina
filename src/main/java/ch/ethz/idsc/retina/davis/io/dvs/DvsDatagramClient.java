// code by jph
package ch.ethz.idsc.retina.davis.io.dvs;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.davis.DavisDecoder;
import ch.ethz.idsc.retina.davis.DvsDavisEventListener;
import ch.ethz.idsc.retina.davis._240c.DvsDavisEvent;

// TODO lot's of magic const in this class
public class DvsDatagramClient {
  private static final int MAX_PACKET_SIZE = 4096; // TODO ensure that no server sends larger packets
  private final DavisDecoder davisDecoder;
  private final List<DvsDavisEventListener> listeners = new LinkedList<>();

  public DvsDatagramClient(DavisDecoder davisDecoder) {
    this.davisDecoder = davisDecoder;
  }

  public void addListener(DvsDavisEventListener dvsDavisEventListener) {
    listeners.add(dvsDavisEventListener);
  }

  private int pacid_prev = -1;

  // @Override
  public void start() {
    try (DatagramSocket datagramSocket = new DatagramSocket(DvsDatagramServer.PORT)) {
      byte[] bytes = new byte[MAX_PACKET_SIZE];
      ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
      byteBuffer.order(davisDecoder.getByteOrder());
      DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
      while (true) {
        datagramSocket.receive(datagramPacket);
        byteBuffer.position(0);
        // TODO check consistency
        int numel = byteBuffer.getShort(); // number of events in packet
        int pacid = byteBuffer.getShort(); // running id of packet
        if (pacid_prev + 1 != pacid)
          System.err.println("dvs packet missing");
        int offset = byteBuffer.getInt();
        for (int count = 0; count < numel; ++count) {
          final int misc = byteBuffer.getShort() & 0xffff;
          final int time = offset + (misc >> 1);
          final int x = byteBuffer.get() & 0xff;
          final int y = byteBuffer.get() & 0xff;
          final int i = misc & 1;
          DvsDavisEvent dvsDavisEvent = new DvsDavisEvent(time, x, y, i);
          listeners.forEach(listener -> listener.dvs(dvsDavisEvent));
        }
        pacid_prev = pacid;
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  // @Override
  public void stop() {
    // close socket
  }
  // public static void main(String[] args) {
  // DvsStandaloneClient dvsStandaloneClient = new DvsStandaloneClient(Davis240c.INSTANCE.createDecoder());
  // dvsStandaloneClient.start();
  // }
}
