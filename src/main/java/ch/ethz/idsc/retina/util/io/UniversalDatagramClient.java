// code by jph
package ch.ethz.idsc.retina.util.io;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.retina.util.StartAndStoppable;

public abstract class UniversalDatagramClient implements StartAndStoppable {
  public static UniversalDatagramClient create(byte[] bytes, int port) {
    return new UniversalDatagramClient(bytes) {
      @Override
      public DatagramSocket openSocket() throws SocketException {
        System.out.println("listening on port=" + port);
        return new DatagramSocket(port);
      }
    };
  }

  public static UniversalDatagramClient create(byte[] bytes, int port, String group) {
    return new UniversalDatagramClient(bytes) {
      @Override
      public DatagramSocket openSocket() throws SocketException, UnknownHostException {
        System.out.println("listening on port=" + port + " " + group);
        return new DatagramSocket(port, InetAddress.getByName(group));
      }
    };
  }
  // ---

  private final byte[] bytes;
  private final List<ByteArrayConsumer> listeners = new LinkedList<>();
  private boolean isLaunched;
  private DatagramSocket datagramSocket;

  private UniversalDatagramClient(byte[] bytes) {
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
          datagramSocket = openSocket();
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
    if (Objects.nonNull(datagramSocket))
      datagramSocket.close(); // according to specs will not throw
  }

  public DatagramSocket datagramSocket() {
    return datagramSocket;
  }

  public abstract DatagramSocket openSocket() throws SocketException, UnknownHostException;
}
