// code by jph
package ch.ethz.idsc.retina.util.io;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.retina.util.StartAndStoppable;

public class UniversalDatagramClient implements StartAndStoppable {
  private final int port;
  private final byte[] bytes;
  private final List<ByteArrayConsumer> listeners = new LinkedList<>();
  private boolean isLaunched;
  private DatagramSocket datagramSocket;

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
        try {
          datagramSocket = new DatagramSocket(port);
          DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
          while (isLaunched) {
            datagramSocket.receive(datagramPacket); // blocking
            listeners.forEach(listener -> listener.accept(bytes, datagramPacket.getLength()));
          }
          datagramSocket.close();
        } catch (Exception exception) {
          System.err.println(exception.getMessage());
        }
        System.out.println("exit thread");
      }
    };
    Thread thread = new Thread(runnable);
    thread.start();
  }

  @Override
  public void stop() {
    isLaunched = false;
    if (Objects.nonNull(datagramSocket)) {
      datagramSocket.close(); // according to specs will not throw
      datagramSocket = null;
    }
  }
}
