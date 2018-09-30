// code by jph
package ch.ethz.idsc.retina.dev.misc;

import java.net.DatagramPacket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.AutoboxDevice;
import ch.ethz.idsc.gokart.core.AutoboxSocket;

public class MiscSocket extends AutoboxSocket<MiscGetEvent, MiscPutEvent> {
  private static final int LOCAL_PORT = 5003;
  private static final int REMOTE_PORT = 5003;
  private static final int SEND_PERIOD_MS = 20; // == 50[Hz]
  // ---
  public static final MiscSocket INSTANCE = new MiscSocket();

  private MiscSocket() {
    super(MiscGetEvent.LENGTH, LOCAL_PORT);
    // ---
    addPutProvider(MiscPutFallback.INSTANCE); // default message: no action
    // ---
    addPutProvider(MiscIgnitionProvider.INSTANCE); // calibration procedue
    addGetListener(MiscIgnitionProvider.INSTANCE); // monitor of comm timeout
    // ---
    // SteerBatteryCharger is obsolete, see comment in SteerBatteryCharger
    // addGetListener(SteerBatteryCharger.INSTANCE); // steering passive when steering battery is charged
  }

  @Override // from AutoboxSocket
  protected MiscGetEvent createGetEvent(ByteBuffer byteBuffer) {
    return new MiscGetEvent(byteBuffer);
  }

  @Override // from AutoboxSocket
  protected long getPutPeriod_ms() {
    return SEND_PERIOD_MS;
  }

  @Override // from AutoboxSocket
  protected DatagramPacket getDatagramPacket(byte[] data) throws UnknownHostException {
    return new DatagramPacket(data, data.length, AutoboxDevice.REMOTE_INET_ADDRESS, REMOTE_PORT);
  }
}
