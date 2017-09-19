// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.io.ByteArrayConsumer;
import ch.ethz.idsc.retina.util.io.DatagramSocketManager;

public enum RimoSocket implements StartAndStoppable, ByteArrayConsumer {
  INSTANCE;
  // ---
  public static final int LOCAL_PORT = 5000;
  public static final String LOCAL_ADDRESS = "192.168.1.1";
  // ---
  public static final int REMOTE_PORT = 5000;
  public static final String REMOTE_ADDRESS = "192.168.1.10";
  // ---
  private final DatagramSocketManager datagramSocketManager = //
      DatagramSocketManager.local(new byte[2 * RimoGetEvent.LENGTH], RimoSocket.LOCAL_PORT, RimoSocket.LOCAL_ADDRESS);
  private final List<RimoGetListener> listeners = new LinkedList<>();

  private RimoSocket() {
    datagramSocketManager.addListener(this);
  }

  public void addListener(RimoGetListener rimoGetListener) {
    listeners.add(rimoGetListener);
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
      RimoGetEvent rimoGetL = new RimoGetEvent(byteBuffer);
      RimoGetEvent rimoGetR = new RimoGetEvent(byteBuffer);
      listeners.forEach(listener -> listener.rimoGet(rimoGetL, rimoGetR));
    } catch (Exception exception) {
      System.out.println("fail decode RimoGet, received=" + length);
      System.err.println(exception.getMessage());
    }
  }

  public void send(RimoPutEvent rimoPutEventL, RimoPutEvent rimoPutEventR) {
    byte data[] = new byte[2 * RimoPutEvent.LENGTH];
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    rimoPutEventL.insert(byteBuffer);
    rimoPutEventR.insert(byteBuffer);
    try {
      DatagramPacket datagramPacket = new DatagramPacket(data, data.length, //
          InetAddress.getByName(RimoSocket.REMOTE_ADDRESS), RimoSocket.REMOTE_PORT);
      datagramSocketManager.send(datagramPacket);
    } catch (Exception exception) {
      // ---
      System.out.println("RIMO SEND FAIL");
      exception.printStackTrace();
      System.exit(0); // TODO
    }
  }
}
