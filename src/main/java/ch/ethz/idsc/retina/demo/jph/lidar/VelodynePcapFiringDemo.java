// code by jph
package ch.ethz.idsc.retina.demo.jph.lidar;

import java.io.IOException;

import ch.ethz.idsc.retina.dev.velodyne.app.VelodynePcapPacketDecoder;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.Hdl32eDecoder;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.Hdl32eUtils;
import ch.ethz.idsc.retina.dev.velodyne.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.dev.velodyne.vlp16.Vlp16Utils;
import ch.ethz.idsc.retina.util.io.PcapParse;
import ch.ethz.idsc.retina.util.io.PcapRealtimePlayback;

enum VelodynePcapFiringDemo {
  ;
  static void _hdl32e() throws IOException {
    VelodynePcapPacketDecoder velodynePcapPacketDecoder = VelodynePcapPacketDecoder.hdl32e();
    Hdl32eUtils.createRayFrame( //
        (Hdl32eDecoder) velodynePcapPacketDecoder.velodyneDecoder);
    // ---
    PcapParse.of(Hdl32ePcap.HIGHWAY.file, new PcapRealtimePlayback(1), velodynePcapPacketDecoder); // blocking
  }

  static void _vlp16() throws IOException {
    VelodynePcapPacketDecoder velodynePcapPacketDecoder = VelodynePcapPacketDecoder.vlp16();
    Vlp16Utils.createRayFrame( //
        (Vlp16Decoder) velodynePcapPacketDecoder.velodyneDecoder);
    // ---
    PcapParse.of(Vlp16Pcap.DEPOT_DUAL.file, new PcapRealtimePlayback(1), velodynePcapPacketDecoder); // blocking
  }

  public static void main(String[] args) throws Exception {
    // _hdl32e();
    _vlp16();
  }
}
