// code by jph
package ch.ethz.idsc.retina.dev.davis.data;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.davis.io.DavisDatagram;

// TODO lot's of magic const in this class
public class DavisDvsDatagramClient {
  public final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();

  public void start() {
    try (DatagramSocket datagramSocket = new DatagramSocket(DavisDatagram.DVS_PORT)) {
      byte[] bytes = new byte[DavisDvsBlockCollector.MAX_LENGTH];
      ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
      DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
      while (true) {
        datagramSocket.receive(datagramPacket);
        byteBuffer.position(0);
        davisDvsDatagramDecoder.decode(byteBuffer);
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public void stop() {
  }
}
