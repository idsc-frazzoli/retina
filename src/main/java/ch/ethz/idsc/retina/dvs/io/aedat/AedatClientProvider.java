// code by jph
package ch.ethz.idsc.retina.dvs.io.aedat;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.davis.DavisDecoder;
import ch.ethz.idsc.retina.dev.davis.DavisEventProvider;

public class AedatClientProvider implements DavisEventProvider {
  private static final int MAX_PACKET_SIZE = 8192; // TODO ensure that no server sends larger packets
  private final DavisDecoder davisDecoder;

  public AedatClientProvider(DavisDecoder davisDecoder) {
    this.davisDecoder = davisDecoder;
  }

  @Override
  public void start() {
    try (DatagramSocket datagramSocket = new DatagramSocket(AedatFileSocket.PORT)) {
      byte[] bytes = new byte[MAX_PACKET_SIZE];
      ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
      byteBuffer.order(davisDecoder.getByteOrder());
      DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
      while (true) {
        datagramSocket.receive(datagramPacket);
        byteBuffer.position(0);
        while (byteBuffer.position() < datagramPacket.getLength())
          davisDecoder.read(byteBuffer);
      }
    } catch (Exception exception) {
      // ---
    }
  }

  @Override
  public void stop() {
    // close socket
  }
}
