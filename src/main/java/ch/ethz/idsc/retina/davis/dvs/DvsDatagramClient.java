// code by jph
package ch.ethz.idsc.retina.davis.dvs;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.davis.io.DavisDatagram;

// TODO lot's of magic const in this class
public class DvsDatagramClient {
  public final DvsDatagramDecoder dvsDatagramDecoder = new DvsDatagramDecoder();

  public void start() {
    try (DatagramSocket datagramSocket = new DatagramSocket(DavisDatagram.DVS_PORT)) {
      byte[] bytes = new byte[DvsBlockCollector.MAX_LENGTH];
      ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
      DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
      while (true) {
        datagramSocket.receive(datagramPacket);
        byteBuffer.position(0);
        dvsDatagramDecoder.decode(byteBuffer);
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public void stop() {
  }
}
