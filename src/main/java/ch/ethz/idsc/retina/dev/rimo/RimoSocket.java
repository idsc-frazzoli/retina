// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.gui.gokart.AutoboxSocket;
import ch.ethz.idsc.retina.util.io.DatagramSocketManager;

public class RimoSocket extends AutoboxSocket<RimoGetListener> {
  public static final RimoSocket INSTANCE = new RimoSocket();
  // ---
  private static final int LOCAL_PORT = 5000;
  private static final String LOCAL_ADDRESS = "192.168.1.1";
  // ---
  private static final int REMOTE_PORT = 5000;
  private static final String REMOTE_ADDRESS = "192.168.1.10";
  // ---

  private RimoSocket() {
    super(DatagramSocketManager.local(new byte[2 * RimoGetEvent.LENGTH], RimoSocket.LOCAL_PORT, RimoSocket.LOCAL_ADDRESS));
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

  public void send(RimoPutEvent rimoPutEvent) {
    byte data[] = new byte[2 * RimoPutTire.LENGTH];
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    rimoPutEvent.insert(byteBuffer);
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
