// code by jph
package ch.ethz.idsc.retina.davis.io.aps;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.retina.davis.io.DavisDatagram;

/** sends content of log file in realtime via DatagramSocket */
public class ApsDatagramServer implements ApsBlockListener, AutoCloseable {
  public static final int COLUMNS = 8; // TODO
  // ---
  public final int length;
  // ---
  private DatagramSocket datagramSocket = null;
  private DatagramPacket datagramPacket = null;

  public ApsDatagramServer(ApsBlockCollector apsBlockCollector) {
    apsBlockCollector.setListener(this);
    ByteBuffer byteBuffer = apsBlockCollector.byteBuffer();
    byte[] data = byteBuffer.array();
    length = data.length;
    try {
      datagramSocket = new DatagramSocket();
      // datagramSocket.setTimeToLive(1); // same LAN
      // datagramSocket.setLoopbackMode(false);
      // datagramSocket.setTrafficClass(0x10 + 0x08); // low delay
      datagramPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), DavisDatagram.APS_PORT);
      datagramPacket.setLength(length);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Override
  public void apsBlockReady() {
    try {
      datagramSocket.send(datagramPacket);
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
