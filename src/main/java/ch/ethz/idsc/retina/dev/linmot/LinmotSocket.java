// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.zhkart.AutoboxDevice;
import ch.ethz.idsc.retina.dev.zhkart.AutoboxSocket;

/**  */
public class LinmotSocket extends AutoboxSocket<LinmotGetEvent, LinmotPutEvent> {
  public static final LinmotSocket INSTANCE = new LinmotSocket();
  // ---
  private static final int LOCAL_PORT = 5001;
  // ---
  private static final int REMOTE_PORT = 5001;
  private static final String REMOTE_ADDRESS = AutoboxDevice.REMOTE_ADDRESS;
  // ---
  private static final int SEND_PERIOD_MS = 20;
  // ---

  private LinmotSocket() {
    super(LinmotGetEvent.LENGTH, LOCAL_PORT);
    // ---
    addPutProvider(LinmotCalibrationProvider.INSTANCE);
    addPutProvider(LinmotPutFallback.INSTANCE);
  }

  @Override
  protected LinmotGetEvent createGetEvent(ByteBuffer byteBuffer) {
    return new LinmotGetEvent(byteBuffer);
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
}
