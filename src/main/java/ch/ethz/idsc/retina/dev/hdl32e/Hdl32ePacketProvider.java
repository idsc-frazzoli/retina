// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.util.io.PcapPacketConsumer;

/** default packet distribution
 * 
 * implementation decides based on length of packet to
 * process the data either as firing packet or as GPS */
public class Hdl32ePacketProvider implements PcapPacketConsumer {
  /** the answer to life the universe and everything
   * 
   * hdl32e user's manual refers to first 42 bytes as ethernet header
   * they are only present in pcap file, but not in upd packets from live sensor */
  public static final int _42 = 42;
  public static final int LASER_SIZE1 = 1248;
  public static final int LASER_SIZE2 = 1206;
  public static final int GPS_SIZE1 = 554;
  public static final int GPS_SIZE2 = 512;
  // ---
  public final Hdl32eFiringDecoder hdl32eFiringDecoder = new Hdl32eFiringDecoder();
  public final Hdl32ePositioningDecoder hdl32ePositioningDecoder = new Hdl32ePositioningDecoder();

  @Override
  public void parse(byte[] packet_data, int length) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(packet_data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    if (length == LASER_SIZE1) {
      byteBuffer.position(_42); // skip 42 bytes
      hdl32eFiringDecoder.lasers(byteBuffer);
    } else //
    if (length == LASER_SIZE2) {
      hdl32eFiringDecoder.lasers(byteBuffer);
    }
    if (length == GPS_SIZE1) {
      byteBuffer.position(_42); // skip 42 bytes
      hdl32ePositioningDecoder.positioning(byteBuffer);
    } else //
    if (length == GPS_SIZE2) {
      hdl32ePositioningDecoder.positioning(byteBuffer);
    }
    // else {
    // System.err.println("unhandled packet");
    // }
  }
}
