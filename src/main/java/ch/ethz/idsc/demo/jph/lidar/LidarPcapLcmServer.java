// code by jph
package ch.ethz.idsc.demo.jph.lidar;

import ch.ethz.idsc.gokart.lcm.lidar.VelodynePcapLcmServer;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.util.io.PcapParse;
import ch.ethz.idsc.retina.util.io.PcapRealtimePlayback;

enum LidarPcapLcmServer {
  ;
  /** realtime factor */
  private static final double SPEED = 1.0;

  static void _hdl32e() throws Exception {
    VelodynePcapLcmServer server = new VelodynePcapLcmServer(VelodyneModel.HDL32E, "center");
    PcapParse.of(Hdl32ePcap.HIGHWAY.file, new PcapRealtimePlayback(SPEED), server); // blocking
  }

  static void _vlp16() throws Exception {
    VelodynePcapLcmServer server = new VelodynePcapLcmServer(VelodyneModel.VLP16, "center");
    PcapParse.of(Vlp16Pcap.DOWNTOWN_DUAL.file, new PcapRealtimePlayback(SPEED), server); // blocking
  }

  public static void main(String[] args) throws Exception {
    _hdl32e();
    // _vlp16();
  }
}
