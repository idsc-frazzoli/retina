// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import ch.ethz.idsc.retina.util.io.PcapPacketConsumer;

public class Hdl32eLiveFiringProvider implements Hdl32eLiveProvider {
  public static final int PORT = 2368;
  // ---
  private final Hdl32eFiringPacketConsumer hdl32eFiringPacketConsumer;

  public Hdl32eLiveFiringProvider(Hdl32eFiringPacketConsumer hdl32eFiringPacketConsumer) {
    this.hdl32eFiringPacketConsumer = hdl32eFiringPacketConsumer;
  }

  private boolean isLaunched;

  @Override
  public void start() {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        isLaunched = true;
        System.out.println("live laser");
        try (DatagramSocket datagramSocket = new DatagramSocket(PORT)) {
          byte[] packet_data = new byte[4096];
          DatagramPacket datagramPacket = new DatagramPacket(packet_data, packet_data.length);
          PcapPacketConsumer packetConsumer = new Hdl32ePacketConsumer(hdl32eFiringPacketConsumer, null);
          while (isLaunched) {
            datagramSocket.receive(datagramPacket);
            packetConsumer.parse(packet_data, datagramPacket.getLength());
          }
          datagramSocket.close();
          System.out.println("socket closed.");
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    };
    Thread thread = new Thread(runnable);
    thread.start();
  }

  @Override
  public void stop() {
    isLaunched = false;
  }
}
