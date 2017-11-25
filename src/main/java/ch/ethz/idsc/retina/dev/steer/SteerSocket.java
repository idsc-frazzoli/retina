// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.net.DatagramPacket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.zhkart.AutoboxDevice;
import ch.ethz.idsc.retina.dev.zhkart.AutoboxSocket;

/** socket for communication with the micro-autobox to
 * send commands and receive readings regarding steering */
public class SteerSocket extends AutoboxSocket<SteerGetEvent, SteerPutEvent> {
  /** local */
  private static final int LOCAL_PORT = 5002;
  /** remote */
  private static final int REMOTE_PORT = 5002;
  /* package */ static final int SEND_PERIOD_MS = 20;
  // ---
  public static final SteerSocket INSTANCE = new SteerSocket();
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
    return new DatagramPacket(data, data.length, AutoboxDevice.REMOTE_INET_ADDRESS, REMOTE_PORT);
  }

  public SteerColumnTracker getSteerColumnTracker() {
    return steerColumnTracker;
  }
}
