// code by jph
package ch.ethz.idsc.retina.dev.misc;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.gui.gokart.AutoboxSocket;
import ch.ethz.idsc.retina.util.io.DatagramSocketManager;

public class MiscSocket extends AutoboxSocket<MiscGetListener> {
  public static final MiscSocket INSTANCE = new MiscSocket();
  // ---
  private static final int LOCAL_PORT = 5003;
  private static final String LOCAL_ADDRESS = "192.168.1.1";
  // ---
  private static final int REMOTE_PORT = 5003;
  private static final String REMOTE_ADDRESS = "192.168.1.10";
  // ---

  private MiscSocket() {
    super(DatagramSocketManager.local(new byte[MiscGetEvent.LENGTH], MiscSocket.LOCAL_PORT, MiscSocket.LOCAL_ADDRESS));
    datagramSocketManager.addListener(this);
  }

  @Override
  public void accept(byte[] data, int length) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(data, 0, length);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    MiscGetEvent miscGetEvent = new MiscGetEvent(byteBuffer);
    listeners.forEach(listener -> listener.miscGet(miscGetEvent));
  }

  public void send(MiscPutEvent miscPutEvent) {
    byte[] data = new byte[MiscPutEvent.LENGTH];
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    miscPutEvent.insert(byteBuffer);
    // System.out.println("misc put=" + HexStrings.from(data));
    try {
      DatagramPacket datagramPacket = new DatagramPacket(data, data.length, //
          InetAddress.getByName(MiscSocket.REMOTE_ADDRESS), MiscSocket.REMOTE_PORT);
      datagramSocketManager.send(datagramPacket);
    } catch (Exception exception) {
      // ---
      System.out.println("MISC SEND FAIL");
      exception.printStackTrace();
      System.exit(0); // TODO
    }
  }
}
