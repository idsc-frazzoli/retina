// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

import ch.ethz.idsc.retina.util.io.PcapPacketConsumer;

/** default packet distribution
 * 
 * implementation decides based on length of packet to
 * process the data either as firing packet or as GPS */
public class Hdl32ePacketConsumer implements PcapPacketConsumer {
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
  private final Hdl32eFiringPacketConsumer hdl32eFiringPacketConsumer;
  private final Hdl32ePositioningPacketConsumer hdl32ePositioningPacketConsumer;

  public Hdl32ePacketConsumer( //
      Hdl32eFiringPacketConsumer hdl32eFiringPacketConsumer, //
      Hdl32ePositioningPacketConsumer hdl32eGpsPacketConsumer) {
    this.hdl32eFiringPacketConsumer = hdl32eFiringPacketConsumer;
    this.hdl32ePositioningPacketConsumer = hdl32eGpsPacketConsumer;
  }

  @Override
  public void parse(byte[] packet_data, int length) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(packet_data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    if (Objects.nonNull(hdl32eFiringPacketConsumer))
      if (length == LASER_SIZE1) {
        byteBuffer.position(_42); // skip 42 bytes
        hdl32eFiringPacketConsumer.lasers(byteBuffer);
      } else //
      if (length == LASER_SIZE2) {
        hdl32eFiringPacketConsumer.lasers(byteBuffer);
      }
    if (Objects.nonNull(hdl32ePositioningPacketConsumer))
      if (length == GPS_SIZE1) {
        byteBuffer.position(_42); // skip 42 bytes
        hdl32ePositioningPacketConsumer.positioning(byteBuffer);
      } else //
      if (length == GPS_SIZE2) {
        hdl32ePositioningPacketConsumer.positioning(byteBuffer);
      }
    // else {
    // System.err.println("unhandled packet");
    // }
  }
}
