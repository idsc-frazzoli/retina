// code by jph
package ch.ethz.idsc.retina.util.io;

import java.net.DatagramPacket;
import java.net.InetAddress;

/** demo shows how to send and receive on a single socket */
enum DatagramSocketManagerDemo {
  ;
  public static void main(String[] args) throws Exception {
    final int S1_PORT = 6780;
    final int S2_PORT = 6785;
    final String LADDR = "localhost";
    // ---
    byte[] data = new byte[10];
    DatagramSocketManager s1 = DatagramSocketManager.local(data, S1_PORT, LADDR);
    s1.addListener(new ByteArrayConsumer() {
      @Override
      public void accept(byte[] data, int length) {
        System.out.println("s1 received " + length);
      }
    });
    byte[] data2 = new byte[20];
    DatagramSocketManager s2 = DatagramSocketManager.local(data2, S2_PORT, LADDR);
    s2.addListener(new ByteArrayConsumer() {
      @Override
      public void accept(byte[] data, int length) {
        System.out.println("s2 received " + length);
      }
    });
    // ---
    {
      s1.start();
      s2.start();
      Thread.sleep(200);
      System.out.println("s2 sends");
      s2.send(new DatagramPacket(new byte[5], 5, InetAddress.getByName("localhost"), S1_PORT));
      System.out.println("s1 sends");
      s1.send(new DatagramPacket(new byte[12], 12, InetAddress.getByName("localhost"), S2_PORT));
      Thread.sleep(200);
      s1.stop();
      s2.stop();
    }
    // ---
    Thread.sleep(500);
    {
      s1.start();
      s2.start();
      Thread.sleep(200);
      System.out.println("s2 sends");
      s2.send(new DatagramPacket(new byte[5], 5, InetAddress.getByName("localhost"), S1_PORT));
      System.out.println("s1 sends");
      s1.send(new DatagramPacket(new byte[12], 12, InetAddress.getByName("localhost"), S2_PORT));
      Thread.sleep(200);
      s1.stop();
      s2.stop();
    }
  }
}
