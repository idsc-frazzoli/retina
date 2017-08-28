// code by jph
package ch.ethz.idsc.retina.demo.jph.lidar;

import java.io.IOException;

import ch.ethz.idsc.retina.dev.velodyne.app.VelodynePcapPacketListener;
import ch.ethz.idsc.retina.dev.velodyne.app.VelodyneUtils;
import ch.ethz.idsc.retina.util.io.PcapParse;
import ch.ethz.idsc.retina.util.io.PcapRealtimePlayback;

enum VelodynePcapFiringDemo {
  ;
  static void _hdl32e() throws IOException {
    VelodynePcapPacketListener velodynePcapPacketListener = VelodynePcapPacketListener.hdl32e();
    VelodyneUtils.createRayFrame( //
        VelodyneUtils.createCollector32(velodynePcapPacketListener.velodyneDecoder), //
        velodynePcapPacketListener.velodyneDecoder);
    // ---
    PcapParse.of(Hdl32ePcap.HIGHWAY.file, new PcapRealtimePlayback(1), velodynePcapPacketListener); // blocking
  }

  static void _vlp16() throws IOException {
    VelodynePcapPacketListener velodynePcapPacketListener = VelodynePcapPacketListener.vlp16();
    VelodyneUtils.createRayFrame( //
        VelodyneUtils.createCollector16(velodynePcapPacketListener.velodyneDecoder), //
        velodynePcapPacketListener.velodyneDecoder);
    // ---
    PcapParse.of(Vlp16Pcap.DEPOT_DUAL.file, new PcapRealtimePlayback(1), velodynePcapPacketListener); // blocking
  }

  public static void main(String[] args) throws Exception {
    // _hdl32e();
    _vlp16();
  }
}
