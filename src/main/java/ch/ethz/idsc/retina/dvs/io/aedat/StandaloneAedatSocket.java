// code by jph
package ch.ethz.idsc.retina.dvs.io.aedat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

/** sends content of log file in realtime via DatagramSocket */
public enum StandaloneAedatSocket implements AutoCloseable {
  INSTANCE;
  public static final int PORT = 14321;
  public static final int BUFFER_SIZE = 2 * 512; // -> 8 * 512 == 4096 packet size seems to cause the max sleep time
  // ---
  private final byte[] bytes = new byte[BUFFER_SIZE];
  private final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
  private DatagramSocket datagramSocket = null;
  private DatagramPacket datagramPacket = null;
  private int last_time = 0;

  private StandaloneAedatSocket() {
    byteBuffer.order(ByteOrder.BIG_ENDIAN);
    try {
      datagramSocket = new DatagramSocket();
      // datagramSocket.setTimeToLive(1); // same LAN
      // datagramSocket.setLoopbackMode(false);
      // datagramSocket.setTrafficClass(0x10 + 0x08); // low delay
      datagramPacket = new DatagramPacket(bytes, bytes.length, InetAddress.getLocalHost(), PORT);
      System.out.println("CONNECTION OK");
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  /** @param length
   * @param data
   * @param time */
  public void append(int length, int[] data, int[] time) {
    if (data.length != time.length)
      throw new RuntimeException();
    for (int index = 0; index < length; ++index)
      append(data[index], time[index]);
  }

  private void append(int data, int time) {
    if (time < last_time)
      System.err.println("jump " + (last_time - time));
    last_time = time;
    // ---
    // boolean isDvs = (data & 0x80000000) == 0;
    // final int read = (data >> 10) & 0x3;
    // if (!isDvs && (read == 1))
    {
      byteBuffer.putInt(data);
      byteBuffer.putInt(time);
      if (byteBuffer.position() == BUFFER_SIZE) {
        if (Objects.nonNull(datagramPacket)) {
          datagramPacket.setLength(BUFFER_SIZE);
          try {
            datagramSocket.send(datagramPacket);
          } catch (IOException exception) {
            System.err.println("packet not sent");
          }
        }
        byteBuffer.position(0);
      }
    }
  }

  @Override
  public void close() throws Exception {
    if (Objects.nonNull(datagramSocket))
      datagramSocket.close();
  }
}
