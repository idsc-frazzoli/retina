// code by jph
package ch.ethz.idsc.retina.demo.jph.lidar;

import ch.ethz.idsc.retina.dev.hdl32e.VelodyneModel;
import ch.ethz.idsc.retina.lcm.lidar.VelodynePcapLcmServer;
import ch.ethz.idsc.retina.util.io.PcapParse;

enum LidarPcapLcmServer {
  ;
  static void _hdl32e() throws Exception {
    VelodynePcapLcmServer server = new VelodynePcapLcmServer(VelodyneModel.HDL32E, "center", 1);
    PcapParse.of(Hdl32ePcap.HIGHWAY.file, server); // blocking
  }

  static void _vlp16() throws Exception {
    VelodynePcapLcmServer server = new VelodynePcapLcmServer(VelodyneModel.VLP16, "center", 1);
    PcapParse.of(Vlp16Pcap.DOWNTOWN_SINGLE.file, server); // blocking
  }

  public static void main(String[] args) throws Exception {
    _vlp16();
  }
}
