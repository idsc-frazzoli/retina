// code by jph
package ch.ethz.idsc.retina.davis.aps;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.davis.io.DavisDatagram;

public class ApsDatagramClient {
  // TODO ensure that no server sends larger packets
  private static final int MAX_PACKET_SIZE = 2048;
  // ---
  public final ApsDatagramDecoder apsDatagramDecoder = new ApsDatagramDecoder();

  public void start() {
    try (DatagramSocket datagramSocket = new DatagramSocket(DavisDatagram.APS_PORT)) {
      byte[] bytes = new byte[MAX_PACKET_SIZE];
      ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
      DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
      while (true) {
        datagramSocket.receive(datagramPacket);
        byteBuffer.position(0);
        apsDatagramDecoder.decode(byteBuffer);
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public void stop() {
    // close socket
  }
}
