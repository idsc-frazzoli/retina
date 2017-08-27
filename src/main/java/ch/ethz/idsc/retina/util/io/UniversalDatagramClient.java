// code by jph
package ch.ethz.idsc.retina.util.io;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.util.StartAndStoppable;

public class UniversalDatagramClient implements StartAndStoppable {
  private final int port;
  private final byte[] bytes;
  private final List<ByteArrayConsumer> listeners = new LinkedList<>();
  private boolean isLaunched;

  public UniversalDatagramClient(int port, byte[] bytes) {
    this.port = port;
    this.bytes = bytes;
  }

  public void addListener(ByteArrayConsumer byteArrayConsumer) {
    listeners.add(byteArrayConsumer);
  }

  @Override
  public void start() {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        isLaunched = true;
        try (DatagramSocket datagramSocket = new DatagramSocket(port)) {
          DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
          while (isLaunched) {
            datagramSocket.receive(datagramPacket); // blocking TODO how to clean unblock?
            listeners.forEach(listener -> listener.accept(bytes, datagramPacket.getLength()));
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
