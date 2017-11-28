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

/** implementation hosts a thread to listen to incoming messages
 * 
 * <ul>
 * <li>the socket is opened when start() is called
 * <li>the socket is closed when stop() is called
 * </ul>
 * 
 * <p>reception callback and send() are available only when the socket is open,
 * i.e. in between a start() and a stop() call
 * 
 * <p>the implementation is used to communicate with the Velodyne lidars, Quanergy
 * lidars, and the dSpace micro-Autobox */
public abstract class DatagramSocketManager implements StartAndStoppable {
  /** Quote from DatagramSocket javadoc:
   * 
   * Creates a datagram socket, bound to the specified local address. The local
   * port must be between 0 and 65535 inclusive. If the IP address is 0.0.0.0, the
   * socket will be bound to the {@link InetAddress#isAnyLocalAddress wildcard}
   * address, an IP address chosen by the kernel.
   *
   * @param bytes container of sufficient size to receive datagram packets
   * @param port local port to use
   * @param laddr local address to bind */
  public static DatagramSocketManager local(byte[] bytes, int port, String laddr) {
    return new DatagramSocketManager(bytes) {
      @Override
      DatagramSocket private_createSocket() throws SocketException, UnknownHostException {
        return new DatagramSocket(port, InetAddress.getByName(laddr));
      }
    };
  }

  /** the velodyne sensor does send data via connections that specify a local address
   * 
   * @param bytes
   * @param port
   * @return */
  public static DatagramSocketManager local(byte[] bytes, int port) {
    return new DatagramSocketManager(bytes) {
      @Override
      DatagramSocket private_createSocket() throws SocketException {
        return new DatagramSocket(port);
      }
    };
  }

  // ---
  private static final String SOCKET_CLOSED = "Socket closed";
  /** bytes for reception of data */
  private final byte[] bytes;
  private final List<ByteArrayConsumer> listeners = new LinkedList<>();
  private DatagramSocket datagramSocket;
  private Thread thread;

  private DatagramSocketManager(byte[] bytes) {
    this.bytes = bytes;
  }

  public void addListener(ByteArrayConsumer byteArrayConsumer) {
    listeners.add(byteArrayConsumer);
  }

  @Override
  public void start() {
    if (Objects.isNull(datagramSocket) || datagramSocket.isClosed())
      try {
        datagramSocket = private_createSocket();
        Runnable runnable = new Runnable() {
          @Override
          public void run() {
            try {
              DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
              while (true) {
                datagramSocket.receive(datagramPacket); // blocking
                listeners.forEach(listener -> listener.accept(bytes, datagramPacket.getLength()));
              }
            } catch (Exception exception) {
              String message = exception.getMessage(); // message may be null
              if (Objects.isNull(message) || !SOCKET_CLOSED.equals(message))
                exception.printStackTrace();
            }
          }
        };
        thread = new Thread(runnable);
        thread.start();
      } catch (Exception exception) {
        exception.printStackTrace();
      }
  }

  @Override
  public void stop() {
    if (Objects.nonNull(datagramSocket)) {
      datagramSocket.close(); // according to specs will not throw
      if (Objects.nonNull(thread))
        while (thread.isAlive())
          try {
            Thread.sleep(1);
          } catch (Exception exception) {
            exception.printStackTrace();
          }
      thread = null;
      datagramSocket = null;
    }
  }

  /** send given datagramPacket via open socket
   * 
   * @param datagramPacket
   * @throws IOException */
  public void send(DatagramPacket datagramPacket) throws IOException {
    if (Objects.isNull(datagramSocket))
      System.err.println("still has to invoke start!");
    datagramSocket.send(datagramPacket);
  }

  /* package for testing */ DatagramSocket datagramSocket() {
    return datagramSocket;
  }

  abstract DatagramSocket private_createSocket() throws SocketException, UnknownHostException;
}
