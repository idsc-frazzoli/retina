// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.util.HexStrings;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.io.ByteArrayConsumer;
import ch.ethz.idsc.retina.util.io.DatagramSocketManager;

/** Example interface on datahaki's computer
 * 
 * enx9cebe8143edb Link encap:Ethernet HWaddr 9c:eb:e8:14:3e:db inet
 * addr:192.168.1.1 Bcast:192.168.1.255 Mask:255.255.255.0 inet6 addr:
 * fe80::9eeb:e8ff:fe14:3edb/64 Scope:Link UP BROADCAST RUNNING MULTICAST
 * MTU:1500 Metric:1 RX packets:466380 errors:0 dropped:0 overruns:0 frame:0 TX
 * packets:233412 errors:0 dropped:0 overruns:0 carrier:0 collisions:0
 * txqueuelen:1000 RX bytes:643249464 (643.2 MB) TX bytes:17275914 (17.2 MB) */
public enum LinmotSocket implements StartAndStoppable, ByteArrayConsumer {
  INSTANCE;
  // ---
  public static final int LOCAL_PORT = 5001;
  public static final String LOCAL_ADDRESS = "192.168.1.1";
  // ---
  public static final int REMOTE_PORT = 5001;
  public static final String REMOTE_ADDRESS = "192.168.1.10";
  // ---
  private final DatagramSocketManager datagramSocketManager = //
      DatagramSocketManager.local(new byte[LinmotGetEvent.LENGTH], LinmotSocket.LOCAL_PORT, LinmotSocket.LOCAL_ADDRESS);
  private final List<LinmotGetListener> listeners = new LinkedList<>();

  private LinmotSocket() {
    datagramSocketManager.addListener(this);
  }

  public void addListener(LinmotGetListener linmotGetListener) {
    listeners.add(linmotGetListener);
  }

  @Override
  public void start() {
    datagramSocketManager.start();
  }

  @Override
  public void stop() {
    datagramSocketManager.stop();
  }

  @Override
  public void accept(byte[] data, int length) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    try {
      LinmotGetEvent linmotGetEvent = new LinmotGetEvent(byteBuffer);
      listeners.forEach(listener -> listener.linmotGet(linmotGetEvent));
    } catch (Exception exception) {
      System.out.println("fail decode, received =" + length + " : " + HexStrings.from(data));
      System.err.println(exception.getMessage());
    }
  }

  public void send(LinmotPutEvent linmotPutEvent) {
    byte[] data = new byte[LinmotPutEvent.LENGTH];
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    linmotPutEvent.insert(byteBuffer);
    try {
      DatagramPacket datagramPacket = new DatagramPacket(data, data.length, //
          InetAddress.getByName(LinmotSocket.REMOTE_ADDRESS), LinmotSocket.REMOTE_PORT);
      datagramSocketManager.send(datagramPacket);
      // System.out.println("linmot put=" + HexStrings.from(data));
    } catch (Exception exception) {
      // ---
      System.out.println("LINMOT SEND FAIL");
      exception.printStackTrace();
      System.exit(0); // TODO
    }
  }
}
