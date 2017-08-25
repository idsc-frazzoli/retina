// code by jph
package ch.ethz.idsc.retina.demo.jph.hdl32e;

import ch.ethz.idsc.retina.lcm.lidar.VelodynePcapLcmServer;
import ch.ethz.idsc.retina.util.io.PcapParse;

enum LidarPcapLcmServer {
  ;
  public static void main(String[] args) throws Exception {
    VelodynePcapLcmServer server = new VelodynePcapLcmServer("hdl32e", "center", 1);
    PcapParse.of(Pcap.BUTTERFIELD.file, server); // blocking
  }
}
