// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import java.net.DatagramPacket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.AutoboxDevice;
import ch.ethz.idsc.gokart.core.AutoboxSocket;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** communication manager to micro-autobox */
public final class RimoSocket extends AutoboxSocket<RimoGetEvent, RimoPutEvent> {
  private static final int LOCAL_PORT = 5000;
  private static final int REMOTE_PORT = 5000;
  // ---
  /** the communication rate affects the torque PI control */
  private static final int SEND_PERIOD_MS = 20; // 50[Hz]

  /** @return 0.004[s] */
  public static Scalar getGetPeriod() {
    return Quantity.of(250, SI.PER_SECOND).reciprocal();
  }

  // ---
  public static final RimoSocket INSTANCE = new RimoSocket();

  private RimoSocket() {
    super(RimoGetEvent.LENGTH, LOCAL_PORT);
    // ---
    // FIXME JPH
    // addGetListener(EmergencyBrakeProvider.INSTANCE);
    // ---
    addPutProvider(RimoPutFallback.INSTANCE);
  }

  @Override // from AutoboxSocket
  protected RimoGetEvent createGetEvent(ByteBuffer byteBuffer) {
    return new RimoGetEvent(byteBuffer);
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
