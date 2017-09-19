// code by jph
package ch.ethz.idsc.retina.dev.misc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.io.ByteArrayConsumer;
import ch.ethz.idsc.retina.util.io.DatagramSocketManager;

public enum MiscSocket implements StartAndStoppable, ByteArrayConsumer {
  INSTANCE;
  // ---
  public static final int LOCAL_PORT = 5003;
  public static final String LOCAL_ADDRESS = "192.168.1.1";
  // ---
  public static final int REMOTE_PORT = 5003;
  public static final String REMOTE_ADDRESS = "192.168.1.10";
  // ---
  private final DatagramSocketManager datagramSocketManager = //
      DatagramSocketManager.local(new byte[MiscGetEvent.LENGTH], MiscSocket.LOCAL_PORT, MiscSocket.LOCAL_ADDRESS);
  private final List<MiscGetListener> listeners = new LinkedList<>();

  private MiscSocket() {
    datagramSocketManager.addListener(this);
  }

  public void addListener(MiscGetListener rimoGetListener) {
    listeners.add(rimoGetListener);
  }

  @Override
  public void start() {
    datagramSocketManager.start();
  }

  public void send(DatagramPacket datagramPacket) throws IOException {
    datagramSocketManager.send(datagramPacket);
  }

  @Override
  public void stop() {
    datagramSocketManager.stop();
  }

  @Override
  public void accept(byte[] data, int length) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(data, 0, length);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    MiscGetEvent miscGetEvent = new MiscGetEvent(byteBuffer);
    listeners.forEach(listener -> listener.miscGet(miscGetEvent));
  }
}
