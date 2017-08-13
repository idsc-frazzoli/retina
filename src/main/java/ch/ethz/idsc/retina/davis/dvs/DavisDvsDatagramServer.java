// code by jph
package ch.ethz.idsc.retina.davis.dvs;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.retina.davis.io.DavisDatagram;

/** sends content of log file in realtime via DatagramSocket */
public class DavisDvsDatagramServer implements DavisDvsBlockListener, AutoCloseable {
  private DatagramSocket datagramSocket = null;
  private DatagramPacket datagramPacket = null;

  public DavisDvsDatagramServer(DavisDvsBlockCollector davisDvsBlockCollector) {
    davisDvsBlockCollector.setListener(this);
    ByteBuffer byteBuffer = davisDvsBlockCollector.byteBuffer();
    byte[] data = byteBuffer.array();
    try {
      datagramSocket = new DatagramSocket();
      // datagramSocket.setTimeToLive(1); // same LAN
      // datagramSocket.setLoopbackMode(false);
      // datagramSocket.setTrafficClass(0x10 + 0x08); // low delay
      datagramPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), DavisDatagram.DVS_PORT);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Override
  public void dvsBlockReady(int length, ByteBuffer byteBuffer) {
    datagramPacket.setLength(length);
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
