// code by jph
package ch.ethz.idsc.retina.lidar.app;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.hdl32e.Hdl32eDecoder;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.util.io.PcapPacketListener;

/** default packet distribution
 * 
 * implementation decides based on length of packet to process the data either
 * as firing packet or as GPS */
public class VelodynePcapPacketListener implements PcapPacketListener {
  public static VelodynePcapPacketListener hdl32e() {
    return new VelodynePcapPacketListener(new Hdl32eDecoder());
  }

  public static VelodynePcapPacketListener vlp16() {
    return new VelodynePcapPacketListener(new Vlp16Decoder());
  }

  // ---
  public final VelodyneDecoder velodyneDecoder;

  public VelodynePcapPacketListener(VelodyneDecoder velodyneDecoder) {
    this.velodyneDecoder = velodyneDecoder;
  }

  @Override
  public void pcapPacket(int sec, int usec, byte[] packet_data, int length) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(packet_data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    /** the answer to life the universe and everything hdl32e user's manual refers to
     * first 42 bytes as ethernet header they are only present in pcap file, but not
     * in upd packets from live sensor */
    byteBuffer.position(42);
    switch (length) {
    case 1248:
      velodyneDecoder.lasers(byteBuffer);
      break;
    case 554:
      velodyneDecoder.positioning(byteBuffer);
      break;
    default:
      System.err.println("unknown length " + length);
    }
  }
}
