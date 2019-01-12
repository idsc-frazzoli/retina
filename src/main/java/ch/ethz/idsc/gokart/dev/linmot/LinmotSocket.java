// code by jph
package ch.ethz.idsc.gokart.dev.linmot;

import java.net.DatagramPacket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.AutoboxDevice;
import ch.ethz.idsc.gokart.core.AutoboxSocket;

/** communication socket to command the brake */
public final class LinmotSocket extends AutoboxSocket<LinmotGetEvent, LinmotPutEvent> {
  private static final int LOCAL_PORT = 5001;
  private static final int REMOTE_PORT = 5001;
  // ---
  private static final int SEND_PERIOD_MS = 20; // <- results in a frequency of 50[Hz]
  // ---
  public static final LinmotSocket INSTANCE = new LinmotSocket();

  private LinmotSocket() {
    super(LinmotGetEvent.LENGTH, LOCAL_PORT);
    // ---
    addPutProvider(LinmotFireFighter.INSTANCE);
    addGetListener(LinmotFireFighter.INSTANCE);
    // ---
    // addPutProvider(EmergencyBrakeProvider.INSTANCE);
    // ---
    addPutProvider(LinmotCalibrationProvider.INSTANCE);
    addGetListener(LinmotCalibrationProvider.INSTANCE);
    // ---
    addPutProvider(LinmotPutFallback.INSTANCE);
  }

  @Override // from AutoboxSocket
  protected LinmotGetEvent createGetEvent(ByteBuffer byteBuffer) {
    return new LinmotGetEvent(byteBuffer);
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
