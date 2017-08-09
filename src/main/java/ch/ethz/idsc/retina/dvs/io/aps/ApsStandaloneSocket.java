// code by jph
package ch.ethz.idsc.retina.dvs.io.aps;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

/** sends content of log file in realtime via DatagramSocket */
public class ApsStandaloneSocket implements ApsBlockListener, AutoCloseable {
  // ---
  public static final int COLUMNS = 8;
  public static final int PORT = 14321;
  public final int BUFFER_SIZE;
  // ---
  private DatagramSocket datagramSocket = null;
  private DatagramPacket datagramPacket = null;

  public ApsStandaloneSocket(ApsColumnCollector columnApsCollector) {
    columnApsCollector.setListener(this);
    ByteBuffer byteBuffer = columnApsCollector.byteBuffer();
    byteBuffer.order(ByteOrder.BIG_ENDIAN);
    byte[] data = byteBuffer.array();
    BUFFER_SIZE = data.length;
    try {
      datagramSocket = new DatagramSocket();
      // datagramSocket.setTimeToLive(1); // same LAN
      // datagramSocket.setLoopbackMode(false);
      // datagramSocket.setTrafficClass(0x10 + 0x08); // low delay
      datagramPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), PORT);
      System.out.println("CONNECTION OK");
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Override
  public void block() {
    datagramPacket.setLength(BUFFER_SIZE); // TODO try if once is sufficient
    try {
      datagramSocket.send(datagramPacket);
      System.out.println("sent.");
    } catch (IOException exception) {
      System.err.println("packet not sent");
    }
  }

  @Override
  public void close() throws Exception {
    if (Objects.nonNull(datagramSocket))
      datagramSocket.close();
  }
}
