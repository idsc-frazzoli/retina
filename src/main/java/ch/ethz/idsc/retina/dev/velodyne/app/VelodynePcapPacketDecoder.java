// code by jph
package ch.ethz.idsc.retina.dev.velodyne.app;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.dev.velodyne.VelodynePosDecoder;
import ch.ethz.idsc.retina.dev.velodyne.VelodyneRayDecoder;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.Hdl32ePosDecoder;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.Hdl32eRayDecoder;
import ch.ethz.idsc.retina.util.io.PcapPacketListener;

/** default packet distribution
 * 
 * implementation decides based on length of packet to
 * process the data either as firing packet or as GPS */
public class VelodynePcapPacketDecoder implements PcapPacketListener {
  public static VelodynePcapPacketDecoder hdl32e() {
    return new VelodynePcapPacketDecoder(new Hdl32eRayDecoder(), new Hdl32ePosDecoder());
  }

  /** the answer to life the universe and everything
   * 
   * hdl32e user's manual refers to first 42 bytes as ethernet header
   * they are only present in pcap file, but not in upd packets from live sensor */
  // ---
  public final VelodyneRayDecoder rayDecoder;
  public final VelodynePosDecoder posDecoder;

  public VelodynePcapPacketDecoder(VelodyneRayDecoder rayDecoder, VelodynePosDecoder posDecoder) {
    this.rayDecoder = rayDecoder;
    this.posDecoder = posDecoder;
  }

  @Override
  public void packet(int sec, int usec, byte[] packet_data, int length) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(packet_data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.position(42);
    switch (length) {
    case 1248:
      rayDecoder.lasers(byteBuffer);
      break;
    case 554:
      posDecoder.positioning(byteBuffer);
      break;
    default:
      System.err.println("unknown length " + length);
    }
  }
}
