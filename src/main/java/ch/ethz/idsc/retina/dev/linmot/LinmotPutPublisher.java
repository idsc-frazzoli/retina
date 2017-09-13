// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.util.HexStrings;

public class LinmotPutPublisher {
  private final byte data[] = new byte[12];
  private final ByteBuffer byteBuffer = ByteBuffer.wrap(data);
  private MulticastSocket multicastSocket;
  private DatagramPacket datagramPacket;

  public LinmotPutPublisher() {
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    // TODO R/N/J magic const
    int port = 5000;
    // TODO R/N/J address
    String group = "225.4.5.6";
    try {
      multicastSocket = new MulticastSocket();
      multicastSocket.setTimeToLive(1); // TODO R/N/J TTL maybe 2 ?
      datagramPacket = new DatagramPacket(data, data.length, InetAddress.getByName(group), port);
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println("CRITICAL FAILURE: SENDING WON'T WORK!");
    }
  }

  public ByteBuffer byteBuffer() {
    byteBuffer.position(0);
    return byteBuffer;
  }

  public void send() {
    try {
      System.out.println(HexStrings.from(data)); // TODO remove when working
      multicastSocket.send(datagramPacket);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
