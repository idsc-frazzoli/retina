// code by jph
package ch.ethz.idsc.retina.util.io;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.retina.util.StartAndStoppable;

/** class hosts a thread to listen to incoming messages
 * 
 * the socket is opened when start() is called
 * the socket is closed when stop() is called
 * 
 * reception callback and send() are available only when the socket is open,
 * i.e. in between a start() and a stop() call */
public abstract class DatagramSocketManager implements StartAndStoppable {
  public static DatagramSocketManager local(byte[] bytes, int port) {
    return new DatagramSocketManager(bytes) {
      @Override
      DatagramSocket openSocket() throws SocketException {
        System.out.println("listening on port=" + port);
        return new DatagramSocket(port);
      }
    };
  }

  /** Quote from DatagramSocket javadoc:
   * 
   * Creates a datagram socket, bound to the specified local
   * address. The local port must be between 0 and 65535 inclusive.
   * If the IP address is 0.0.0.0, the socket will be bound to the
   * {@link InetAddress#isAnyLocalAddress wildcard} address,
   * an IP address chosen by the kernel.
   *
   * @param bytes container of sufficient size to receive datagram packets
   * @param port local port to use
   * @param laddr local address to bind */
  public static DatagramSocketManager local(byte[] bytes, int port, String laddr) {
    return new DatagramSocketManager(bytes) {
      @Override
      DatagramSocket openSocket() throws SocketException, UnknownHostException {
        System.out.println("listening on port=" + port + " " + laddr);
        return new DatagramSocket(port, InetAddress.getByName(laddr));
      }
    };
  }
  // ---

  /** bytes for reception of data */
  private final byte[] bytes;
  private final List<ByteArrayConsumer> listeners = new LinkedList<>();
  private volatile boolean isLaunched;
  private DatagramSocket datagramSocket;

  private DatagramSocketManager(byte[] bytes) {
    this.bytes = bytes;
  }

  public void addListener(ByteArrayConsumer byteArrayConsumer) {
    listeners.add(byteArrayConsumer);
  }

  @Override
  public void start() {
    isLaunched = true;
    try {
      datagramSocket = openSocket();
      Runnable runnable = new Runnable() {
        @Override
        public void run() {
          try {
            System.out.println("datagramSocket open " + !datagramSocket.isClosed());
            DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
            while (isLaunched) {
              datagramSocket.receive(datagramPacket); // blocking
              listeners.forEach(listener -> listener.accept(bytes, datagramPacket.getLength()));
            }
          } catch (Exception exception) {
            // typically prints: "Socket closed"
            System.err.println(exception.getMessage());
          }
          System.out.println("exit thread");
        }
      };
      Thread thread = new Thread(runnable);
      thread.start();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Override
  public void stop() {
    isLaunched = false;
    if (Objects.nonNull(datagramSocket)) {
      datagramSocket.close(); // according to specs will not throw
      System.out.println("datasocket closed " + datagramSocket.isClosed());
      datagramSocket = null;
    }
  }

  public void send(DatagramPacket datagramPacket) throws IOException {
    if (Objects.isNull(datagramSocket))
      System.err.println("still has to invoke start!");
    datagramSocket.send(datagramPacket);
  }

  /* package for testing */ DatagramSocket datagramSocket() {
    return datagramSocket;
  }

  abstract DatagramSocket openSocket() throws SocketException, UnknownHostException;
}
