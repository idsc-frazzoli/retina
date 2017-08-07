// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.util.io.PacketConsumer;

/** default packet distribution
 * 
 * implementation decides based on length of packet to
 * process the data either as firing packet or as GPS */
public class Hdl32ePacketConsumer implements PacketConsumer {
  /** the answer to life the universe and everything */
  public static final int ADAMS = 42;
  public static final int LASER_SIZE1 = 1248;
  public static final int LASER_SIZE2 = 1206;
  public static final int GPS_SIZE1 = 554;
  public static final int GPS_SIZE2 = 512;
  // ---
  private final Hdl32eFiringPacketConsumer hdl32eFiringPacketConsumer;

  // TODO what about GPS consumers?
  public Hdl32ePacketConsumer(Hdl32eFiringPacketConsumer hdl32eFiringPacketConsumer) {
    this.hdl32eFiringPacketConsumer = hdl32eFiringPacketConsumer;
  }

  @Override
  public void parse(byte[] packet_data, int length) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(packet_data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    if (length == LASER_SIZE1) {
      byteBuffer.position(ADAMS); // skip 42 bytes
      // System.out.println("ok");
      hdl32eFiringPacketConsumer.lasers(byteBuffer);
    } else //
    if (length == LASER_SIZE2) {
      // byteBuffer.position(42);
      // System.out.println("ok");
      hdl32eFiringPacketConsumer.lasers(byteBuffer);
    } else //
    if (length == GPS_SIZE1) {
      byteBuffer.position(ADAMS); // skip 42 bytes
      // TODO
    } else //
    if (length == GPS_SIZE2) {
    } else {
      // TODO 554 GPS
      System.out.println("unhandled " + length);
      // 554 for GPS (?)
      // System.out.println(length);
    }
  }
}
