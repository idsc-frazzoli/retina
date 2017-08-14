// code by jph
package ch.ethz.idsc.retina.davis.data;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.davis.io.DavisDatagram;

public class DavisApsDatagramClient {
  // TODO ensure that no server sends larger packets
  private static final int MAX_PACKET_SIZE = 2048;
  // ---
  public final DavisApsDatagramDecoder davisApsDatagramDecoder = new DavisApsDatagramDecoder();

  public void start() {
    try (DatagramSocket datagramSocket = new DatagramSocket(DavisDatagram.APS_PORT)) {
      byte[] bytes = new byte[MAX_PACKET_SIZE];
      ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
      DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
      while (true) {
        datagramSocket.receive(datagramPacket);
        byteBuffer.position(0);
        davisApsDatagramDecoder.decode(byteBuffer);
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public void stop() {
    // close socket
  }
}
