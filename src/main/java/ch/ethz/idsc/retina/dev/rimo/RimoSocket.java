// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.net.DatagramPacket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.zhkart.AutoboxDevice;
import ch.ethz.idsc.retina.dev.zhkart.AutoboxSocket;

public class RimoSocket extends AutoboxSocket<RimoGetEvent, RimoPutEvent> {
  private static final int LOCAL_PORT = 5000;
  private static final int REMOTE_PORT = 5000;
  // ---
  /** the communication rate affects the torque PI control */
  private static final int SEND_PERIOD_MS = 20; // 50[Hz]
  // ---
  public static final RimoSocket INSTANCE = new RimoSocket();

  private RimoSocket() {
    super(RimoGetEvent.LENGTH, LOCAL_PORT);
    // ---
    addPutProvider(RimoPutFallback.INSTANCE);
  }

  @Override
  protected RimoGetEvent createGetEvent(ByteBuffer byteBuffer) {
    return new RimoGetEvent(byteBuffer);
  }

  @Override
  protected long getPeriod() {
    return SEND_PERIOD_MS;
  }

  @Override
  protected DatagramPacket getDatagramPacket(byte[] data) throws UnknownHostException {
    return new DatagramPacket(data, data.length, AutoboxDevice.REMOTE_INET_ADDRESS, REMOTE_PORT);
  }
}
