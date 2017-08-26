// code by jph
package ch.ethz.idsc.retina.demo.jph.lidar;

import ch.ethz.idsc.retina.dev.velodyne.VelodyneModel;
import ch.ethz.idsc.retina.lcm.lidar.VelodynePcapLcmServer;
import ch.ethz.idsc.retina.util.io.PcapParse;
import ch.ethz.idsc.retina.util.io.PcapRealtimePlayback;

enum LidarPcapLcmServer {
  ;
  /** realtime factor */
  private static final double SPEED = 1.0;

  static void _hdl32e() throws Exception {
    VelodynePcapLcmServer server = new VelodynePcapLcmServer(VelodyneModel.HDL32E, "center", 1);
    PcapParse.of(Hdl32ePcap.HIGHWAY.file, new PcapRealtimePlayback(SPEED), server); // blocking
  }

  static void _vlp16() throws Exception {
    VelodynePcapLcmServer server = new VelodynePcapLcmServer(VelodyneModel.VLP16, "center", 1);
    PcapParse.of(Vlp16Pcap.DOWNTOWN_SINGLE.file, new PcapRealtimePlayback(SPEED), server); // blocking
  }

  public static void main(String[] args) throws Exception {
    _hdl32e();
  }
}
