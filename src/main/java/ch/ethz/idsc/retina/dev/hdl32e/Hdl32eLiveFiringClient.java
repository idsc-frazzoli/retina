// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.core.StartAndStoppable;
import ch.ethz.idsc.retina.util.io.PcapPacketConsumer;

/** HDL32E publishes firing data on port 2368
 * 
 * the Hdl32eLiveFiringClient listens to the data and distributes the data to listeners */
public class Hdl32eLiveFiringClient implements StartAndStoppable {
  public static final int PORT = 2368;
  public static final int LENGTH = 1206;
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
          byte[] bytes = new byte[LENGTH];
          DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
          while (isLaunched) {
            datagramSocket.receive(datagramPacket);
            listeners.forEach(listener -> listener.parse(bytes, datagramPacket.getLength()));
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
