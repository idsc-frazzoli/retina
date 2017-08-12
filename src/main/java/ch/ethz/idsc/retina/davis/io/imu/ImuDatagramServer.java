// code by jph
package ch.ethz.idsc.retina.davis.io.imu;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

import ch.ethz.idsc.retina.davis.io.DavisDatagram;

/** sends content of log file in realtime via DatagramSocket */
public class ImuDatagramServer implements DavisImuFrameListener, AutoCloseable {
  public static final int PACKET_LENGTH = 4 + 2 + 4 * 7;
  // ---
  private DatagramSocket datagramSocket = null;
  private DatagramPacket datagramPacket = null;
  private final ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[PACKET_LENGTH]);
  private short pacid = 0;

  public ImuDatagramServer() {
    byteBuffer.order(ByteOrder.BIG_ENDIAN);
    byte[] data = byteBuffer.array();
    try {
      datagramSocket = new DatagramSocket();
      // datagramSocket.setTimeToLive(1); // same LAN
      // datagramSocket.setLoopbackMode(false);
      // datagramSocket.setTrafficClass(0x10 + 0x08); // low delay
      datagramPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), DavisDatagram.IMU_PORT);
      datagramPacket.setLength(PACKET_LENGTH);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Override
  public void imuFrame(DavisImuFrame davisImuFrame) {
    try {
      byteBuffer.position(0);
      byteBuffer.putInt(davisImuFrame.time);
      byteBuffer.putShort(pacid);
      byteBuffer.putFloat(davisImuFrame.accelX);
      byteBuffer.putFloat(davisImuFrame.accelY);
      byteBuffer.putFloat(davisImuFrame.accelZ);
      byteBuffer.putFloat(davisImuFrame.temperature);
      byteBuffer.putFloat(davisImuFrame.gyroX);
      byteBuffer.putFloat(davisImuFrame.gyroY);
      byteBuffer.putFloat(davisImuFrame.gyroZ);
      datagramSocket.send(datagramPacket);
      ++pacid;
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
