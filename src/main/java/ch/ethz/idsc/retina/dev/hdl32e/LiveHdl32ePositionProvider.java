// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import ch.ethz.idsc.retina.util.io.PacketConsumer;

public class LiveHdl32ePositionProvider implements Hdl32ePositionProvider {
  final Hdl32ePositionListener laserPositionListener;

  public LiveHdl32ePositionProvider(Hdl32ePositionListener laserPositionListener) {
    this.laserPositionListener = laserPositionListener;
  }

  private boolean isLaunched;

  @Override
  public void start() {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        isLaunched = true;
        System.out.println("live laser");
        try (DatagramSocket datagramSocket = new DatagramSocket(2368)) {
          byte[] packet_data = new byte[4096];
          DatagramPacket datagramPacket = new DatagramPacket(packet_data, packet_data.length);
          PacketConsumer packetConsumer = new Hdl32ePacketConsumer( //
              new Hdl32eFiringCollector(laserPositionListener));
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
