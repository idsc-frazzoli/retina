// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.zhkart.AutoboxSocket;
import ch.ethz.idsc.retina.util.io.DatagramSocketManager;

public class RimoSocket extends AutoboxSocket<RimoGetEvent, RimoPutEvent> {
  public static final RimoSocket INSTANCE = new RimoSocket();
  // ---
  private static final int LOCAL_PORT = 5000;
  private static final String LOCAL_ADDRESS = "192.168.1.1";
  // ---
  private static final int REMOTE_PORT = 5000;
  private static final String REMOTE_ADDRESS = "192.168.1.10";
  // ---
  private static final int SEND_PERIOD_MS = 20;
  // ---

  private RimoSocket() {
    super(DatagramSocketManager.local(new byte[RimoGetEvent.LENGTH], RimoSocket.LOCAL_PORT, RimoSocket.LOCAL_ADDRESS));
    // ---
    addProvider(RimoPutFallback.INSTANCE);
  }

  @Override
  protected RimoGetEvent createGetEvent(ByteBuffer byteBuffer) {
    RimoGetTire rimoGetL = new RimoGetTire(byteBuffer);
    RimoGetTire rimoGetR = new RimoGetTire(byteBuffer);
    return new RimoGetEvent(rimoGetL, rimoGetR);
  }

  @Override
  protected long getPeriod() {
    return SEND_PERIOD_MS;
  }

  @Override
  protected DatagramPacket getDatagramPacket(byte[] data) throws UnknownHostException {
    return new DatagramPacket(data, data.length, //
        InetAddress.getByName(RimoSocket.REMOTE_ADDRESS), RimoSocket.REMOTE_PORT);
  }
}
