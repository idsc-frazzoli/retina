// code by jph
package ch.ethz.idsc.retina.util.io;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class UniversalDatagramPublisher {
  private MulticastSocket multicastSocket;
  private DatagramPacket datagramPacket;

  public UniversalDatagramPublisher(byte[] data, String group, int port) {
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
}
