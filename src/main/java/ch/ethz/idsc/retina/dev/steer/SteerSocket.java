// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.io.ByteArrayConsumer;
import ch.ethz.idsc.retina.util.io.DatagramSocketManager;

public enum SteerSocket implements StartAndStoppable, ByteArrayConsumer {
  INSTANCE;
  /** local */
  public static final int LOCAL_PORT = 5002;
  public static final String LOCAL_ADDRESS = "192.168.1.1";
  /** remote */
  public static final int REMOTE_PORT = 5002;
  public static final String REMOTE_ADDRESS = "192.168.1.10";
  // ---
  private final DatagramSocketManager datagramSocketManager = //
      DatagramSocketManager.local(new byte[SteerGetEvent.LENGTH], SteerSocket.LOCAL_PORT, SteerSocket.LOCAL_ADDRESS);
  private final List<SteerGetListener> listeners = new LinkedList<>();

  private SteerSocket() {
    datagramSocketManager.addListener(this);
  }

  public void addListener(SteerGetListener steerGetListener) {
    listeners.add(steerGetListener);
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
