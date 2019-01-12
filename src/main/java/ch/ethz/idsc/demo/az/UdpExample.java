// code by az
package ch.ethz.idsc.demo.az;

import java.net.DatagramPacket;
import java.net.InetAddress;

import ch.ethz.idsc.retina.util.io.ByteArrayConsumer;
import ch.ethz.idsc.retina.util.io.DatagramSocketManager;

/* package */ enum UdpExample {
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
    // ---
    {
      s1.start();
      byte[] data2send = new byte[43200];
      for (int count = 0; count < 100; ++count) {
        Thread.sleep(200);
        System.out.println("s1 sends");
        data2send[0] = (byte) count;
        s1.send(new DatagramPacket(data2send, data2send.length, InetAddress.getByName("localhost"), S2_PORT));
        Thread.sleep(200);
      }
      s1.stop();
    }
  }
}
