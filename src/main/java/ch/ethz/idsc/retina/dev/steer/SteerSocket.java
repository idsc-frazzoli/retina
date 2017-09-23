// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.dev.zhkart.AutoboxSocket;
import ch.ethz.idsc.retina.util.io.DatagramSocketManager;

public class SteerSocket extends AutoboxSocket<SteerGetEvent, SteerGetListener, SteerPutEvent, SteerPutProvider> {
  public static final SteerSocket INSTANCE = new SteerSocket();
  /** local */
  private static final int LOCAL_PORT = 5002;
  private static final String LOCAL_ADDRESS = "192.168.1.1";
  /** remote */
  private static final int REMOTE_PORT = 5002;
  private static final String REMOTE_ADDRESS = "192.168.1.10";
  // ---
  private static final int SEND_PERIOD_MS = 20;
  // ---

  private SteerSocket() {
    super(DatagramSocketManager.local(new byte[SteerGetEvent.LENGTH], SteerSocket.LOCAL_PORT, SteerSocket.LOCAL_ADDRESS));
    // ---
    addProvider(SteerPutFallback.INSTANCE);
  }

  @Override
  protected SteerGetEvent createGetEvent(ByteBuffer byteBuffer) {
    return new SteerGetEvent(byteBuffer);
  }

  @Override
  protected long getPeriod() {
    return SEND_PERIOD_MS;
  }

  @Override
  protected DatagramPacket getDatagramPacket(SteerPutEvent steerPutEvent) throws UnknownHostException {
    byte[] data = new byte[SteerPutEvent.LENGTH];
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    steerPutEvent.insert(byteBuffer);
    return new DatagramPacket(data, data.length, //
        InetAddress.getByName(SteerSocket.REMOTE_ADDRESS), SteerSocket.REMOTE_PORT);
  }
}
