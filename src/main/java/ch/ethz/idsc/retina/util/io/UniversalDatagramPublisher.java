// code by jph
package ch.ethz.idsc.retina.util.io;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

@Deprecated
public class UniversalDatagramPublisher {
  private final byte[] data;
  private final String group;
  private final int port;
  private MulticastSocket multicastSocket;
  private DatagramPacket datagramPacket;

  public UniversalDatagramPublisher(byte[] data, String group, int port) {
    this.data = data;
    this.group = group;
    this.port = port;
    try {
      multicastSocket = new MulticastSocket();
      multicastSocket.setTimeToLive(1); // TODO R/N/J TTL maybe 2 ?
      datagramPacket = new DatagramPacket(data, data.length, InetAddress.getByName(group), port);
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println("CRITICAL FAILURE: SENDING WON'T WORK!");
    }
  }

  public void send() {
    try {
      multicastSocket.send(datagramPacket);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
  // @Override
  // public void start() {
  // }
  //
  // @Override
  // public void stop() {
  // multicastSocket.close();
  // }
}
