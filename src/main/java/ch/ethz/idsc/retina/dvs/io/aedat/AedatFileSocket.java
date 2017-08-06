// code by jph
package ch.ethz.idsc.retina.dvs.io.aedat;

import java.io.File;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

import ch.ethz.idsc.retina.dev.davis.DavisDecoder;
import ch.ethz.idsc.retina.dev.davis.DavisEventProvider;
import ch.ethz.idsc.retina.dev.davis._240c.RealtimeSleeper;

/** sends content of log file in realtime via DatagramSocket */
public class AedatFileSocket implements DavisEventProvider {
  public static final int PORT = 14321;
  public static final int BUFFER_SIZE = 8 * 512; // -> 8 * 512 == 4096 packet size seems to cause the max sleep time
  // ---
  private final byte[] bytes = new byte[BUFFER_SIZE];
  private final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
  private final InputStream inputStream;
  private DatagramSocket datagramSocket;
  private final double speed;

  public AedatFileSocket(File file, DavisDecoder davisDecoder, double speed) throws Exception {
    AedatFileHeader aedatFileHeader = new AedatFileHeader(file);
    inputStream = aedatFileHeader.getInputStream();
    byteBuffer.order(ByteOrder.BIG_ENDIAN);
    this.speed = speed;
  }

  @Override
  public void start() {
    try {
      RealtimeSleeper realtimeSleeper = new RealtimeSleeper(speed);
      datagramSocket = new DatagramSocket();
      DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, InetAddress.getLocalHost(), PORT);
      long total = 0;
      while (true) {
        int available = inputStream.read(bytes, 0, bytes.length);
        if (available < 2) // end of file, at least 2 bytes are required for next decoding
          break;
        if (available % 2 == 1) {
          System.err.println("last byte dropped");
          --available;
        }
        // TODO implement filter option to drop APS reset reads
        realtimeSleeper.now(byteBuffer.getInt(4)); // TODO not generic time extraction
        datagramPacket.setLength(available);
        datagramSocket.send(datagramPacket);
        total += available;
      }
      System.out.println(String.format("total sleep: %.3f", realtimeSleeper.getSleepTotalSec()));
      System.out.println(total + " bytes");
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Override
  public void stop() {
    try {
      inputStream.close();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    if (Objects.nonNull(datagramSocket))
      datagramSocket.close();
  }
}
