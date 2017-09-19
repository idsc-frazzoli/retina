// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.gui.gokart.AutoboxSocket;
import ch.ethz.idsc.retina.util.io.DatagramSocketManager;

public class SteerSocket extends AutoboxSocket<SteerGetListener> {
  public static final SteerSocket INSTANCE = new SteerSocket();
  /** local */
  private static final int LOCAL_PORT = 5002;
  private static final String LOCAL_ADDRESS = "192.168.1.1";
  /** remote */
  private static final int REMOTE_PORT = 5002;
  private static final String REMOTE_ADDRESS = "192.168.1.10";
  // ---

  private SteerSocket() {
    super(DatagramSocketManager.local(new byte[SteerGetEvent.LENGTH], SteerSocket.LOCAL_PORT, SteerSocket.LOCAL_ADDRESS));
    datagramSocketManager.addListener(this);
  }

  @Override
  public void accept(byte[] data, int length) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(data, 0, length);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    SteerGetEvent steerGetEvent = new SteerGetEvent(byteBuffer);
    listeners.forEach(listener -> listener.steerGet(steerGetEvent));
  }

  public void send(SteerPutEvent steerPutEvent) {
    byte[] data = new byte[SteerPutEvent.LENGTH];
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    steerPutEvent.insert(byteBuffer);
    // System.out.println("steer put=" + HexStrings.from(data));
    try {
      DatagramPacket datagramPacket = new DatagramPacket(data, data.length, //
          InetAddress.getByName(SteerSocket.REMOTE_ADDRESS), SteerSocket.REMOTE_PORT);
      datagramSocketManager.send(datagramPacket);
    } catch (Exception exception) {
      // ---
      System.out.println("STEER SEND FAIL");
      exception.printStackTrace();
      System.exit(0); // TODO
    }
  }
}
