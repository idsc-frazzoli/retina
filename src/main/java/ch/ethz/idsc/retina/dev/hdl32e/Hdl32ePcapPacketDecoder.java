// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.util.io.PcapPacketListener;

/** default packet distribution
 * 
 * implementation decides based on length of packet to
 * process the data either as firing packet or as GPS */
public class Hdl32ePcapPacketDecoder implements PcapPacketListener {
  /** the answer to life the universe and everything
   * 
   * hdl32e user's manual refers to first 42 bytes as ethernet header
   * they are only present in pcap file, but not in upd packets from live sensor */
  // ---
  public final Hdl32eRayDecoder hdl32eRayDecoder = new Hdl32eRayDecoder();
  public final Hdl32ePosDecoder hdl32ePosDecoder = new Hdl32ePosDecoder();

  @Override
  public void packet(int sec, int usec, byte[] packet_data, int length) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(packet_data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.position(42);
    switch (length) {
    case 1248:
      hdl32eRayDecoder.lasers(byteBuffer);
      break;
    case 554:
      hdl32ePosDecoder.positioning(byteBuffer);
      break;
    default:
      System.err.println("unknown length " + length);
    }
  }
}
