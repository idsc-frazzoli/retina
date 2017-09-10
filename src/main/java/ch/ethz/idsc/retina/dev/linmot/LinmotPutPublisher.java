// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class LinmotPutPublisher {
  private final byte data[] = new byte[12];
  private final ByteBuffer byteBuffer = ByteBuffer.wrap(data);
  private MulticastSocket multicastSocket;
  private DatagramPacket datagramPacket;

  public LinmotPutPublisher() {
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    // TODO magic const
    int port = 5000;
    // Which address
    String group = "225.4.5.6";
    try {
      multicastSocket = new MulticastSocket();
      multicastSocket.setTimeToLive(1); // TTL
      datagramPacket = new DatagramPacket(data, data.length, InetAddress.getByName(group), port);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public ByteBuffer byteBuffer() {
    return byteBuffer;
  }

  public void send() {
    try {
      multicastSocket.send(datagramPacket);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
