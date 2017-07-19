package ch.ethz.idsc.retina.dev.hdl32e;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ConnectionAttempt {
  public static void main(String[] args) throws Exception {
    try (DatagramSocket datagramSocket = new DatagramSocket(2368)) {
      DatagramPacket datagramPacket = new DatagramPacket(new byte[10000], 10000);
      while (true) {
        datagramSocket.receive(datagramPacket);
        System.out.println(datagramPacket.getLength()); // 1206
        System.out.println("here");
      }
    }
  }
}
