// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.core.StartAndStoppable;
import ch.ethz.idsc.retina.util.GlobalAssert;
import ch.ethz.idsc.retina.util.io.PcapPacketConsumer;

public class Hdl32eLivePositioningClient implements StartAndStoppable {
  public static final int PORT = 8308;
  public static final int LENGTH = 512;
  // ---
  private final List<PcapPacketConsumer> listeners = new LinkedList<>();
  private boolean isLaunched;

  public void addListener(PcapPacketConsumer pcapPacketConsumer) {
    listeners.add(pcapPacketConsumer);
  }

  @Override
  public void start() {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        isLaunched = true;
        try (DatagramSocket datagramSocket = new DatagramSocket(PORT)) {
          byte[] packet_data = new byte[LENGTH];
          DatagramPacket datagramPacket = new DatagramPacket(packet_data, packet_data.length);
          while (isLaunched) {
            datagramSocket.receive(datagramPacket); // TODO how to interrupt block?
            GlobalAssert.that(datagramPacket.getLength() == LENGTH);
            listeners.forEach(listener -> listener.parse(packet_data, datagramPacket.getLength()));
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
