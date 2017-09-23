// code by jph
package ch.ethz.idsc.retina.dev.misc;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.dev.zhkart.AutoboxSocket;
import ch.ethz.idsc.retina.util.io.DatagramSocketManager;

public class MiscSocket extends AutoboxSocket<MiscGetEvent, MiscGetListener, MiscPutEvent, MiscPutProvider> {
  public static final MiscSocket INSTANCE = new MiscSocket();
  // ---
  private static final int LOCAL_PORT = 5003;
  private static final String LOCAL_ADDRESS = "192.168.1.1";
  // ---
  private static final int REMOTE_PORT = 5003;
  private static final String REMOTE_ADDRESS = "192.168.1.10";
  // ---
  private static final int SEND_PERIOD_MS = 20;
  // ---

  private MiscSocket() {
    super(DatagramSocketManager.local(new byte[MiscGetEvent.LENGTH], MiscSocket.LOCAL_PORT, MiscSocket.LOCAL_ADDRESS));
    // ---
    addProvider(MiscPutFallback.INSTANCE);
  }

  @Override
  protected MiscGetEvent createGetEvent(ByteBuffer byteBuffer) {
    return new MiscGetEvent(byteBuffer);
  }

  @Override
  protected long getPeriod() {
    return SEND_PERIOD_MS;
  }

  @Override
  protected DatagramPacket getDatagramPacket(MiscPutEvent miscPutEvent) throws UnknownHostException {
    byte[] data = new byte[MiscPutEvent.LENGTH];
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    miscPutEvent.insert(byteBuffer);
    return new DatagramPacket(data, data.length, //
        InetAddress.getByName(MiscSocket.REMOTE_ADDRESS), MiscSocket.REMOTE_PORT);
  }
}
