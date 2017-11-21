// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.zhkart.AutoboxDevice;
import ch.ethz.idsc.retina.dev.zhkart.AutoboxSocket;

public class SteerSocket extends AutoboxSocket<SteerGetEvent, SteerPutEvent> {
  public static final SteerSocket INSTANCE = new SteerSocket();
  /** local */
  private static final int LOCAL_PORT = 5002;
  /** remote */
  private static final int REMOTE_PORT = 5002;
  private static final String REMOTE_ADDRESS = AutoboxDevice.REMOTE_ADDRESS;
  // ---
  public static final int SEND_PERIOD_MS = 20;
  // ---
  private final SteerColumnTracker steerColumnTracker = new SteerColumnTracker();

  private SteerSocket() {
    super(SteerGetEvent.LENGTH, LOCAL_PORT);
    // ---
    addGetListener(steerColumnTracker);
    addPutProvider(SteerPutFallback.INSTANCE);
    addPutProvider(SteerCalibrationProvider.INSTANCE);
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
  protected DatagramPacket getDatagramPacket(byte[] data) throws UnknownHostException {
    return new DatagramPacket(data, data.length, //
        InetAddress.getByName(REMOTE_ADDRESS), REMOTE_PORT);
  }

  public SteerColumnTracker getSteerColumnTracker() {
    return steerColumnTracker;
  }
}
