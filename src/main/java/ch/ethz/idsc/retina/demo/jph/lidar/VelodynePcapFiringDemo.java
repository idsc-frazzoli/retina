// code by jph
package ch.ethz.idsc.retina.demo.jph.lidar;

import java.io.IOException;

import ch.ethz.idsc.retina.dev.velodyne.app.VelodynePcapPacketDecoder;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.Hdl32eUtils;
import ch.ethz.idsc.retina.util.io.PcapParse;
import ch.ethz.idsc.retina.util.io.PcapRealtimePlayback;

enum VelodynePcapFiringDemo {
  ;
  static void _hdl32e() throws IOException {
    VelodynePcapPacketDecoder hdl32ePacketProvider = VelodynePcapPacketDecoder.hdl32e();
    Hdl32eUtils.createRayFrame(hdl32ePacketProvider.rayDecoder, hdl32ePacketProvider.posDecoder);
    // ---
    PcapParse.of(Hdl32ePcap.HIGHWAY.file, new PcapRealtimePlayback(1), hdl32ePacketProvider); // blocking
  }

  public static void main(String[] args) throws Exception {
    _hdl32e();
  }
}
