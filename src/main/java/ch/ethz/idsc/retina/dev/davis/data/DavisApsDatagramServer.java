// code by jph
package ch.ethz.idsc.retina.dev.davis.data;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.retina.dev.davis.io.DavisDatagram;

/** sends content of log file in realtime via DatagramSocket */
public class DavisApsDatagramServer implements DavisApsBlockListener, AutoCloseable {
  private DatagramSocket datagramSocket = null;
  private DatagramPacket datagramPacket = null;

  public DavisApsDatagramServer(DavisApsBlockCollector davisApsBlockCollector) {
    davisApsBlockCollector.setListener(this); // TODO design dubious
    ByteBuffer byteBuffer = davisApsBlockCollector.byteBuffer();
    byte[] data = byteBuffer.array();
    int length = data.length;
    try {
      datagramSocket = new DatagramSocket();
      datagramPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), DavisDatagram.APS_PORT);
      datagramPacket.setLength(length);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Override
  public void apsBlock(ByteBuffer byteBuffer) {
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
