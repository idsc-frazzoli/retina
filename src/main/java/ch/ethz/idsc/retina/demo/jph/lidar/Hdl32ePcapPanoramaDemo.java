// code by jph
package ch.ethz.idsc.retina.demo.jph.lidar;

import ch.ethz.idsc.retina.dev.lidar.app.LidarPanoramaFrame;
import ch.ethz.idsc.retina.dev.lidar.app.VelodynePcapPacketListener;
import ch.ethz.idsc.retina.dev.lidar.hdl32e.Hdl32ePanoramaCollector;
import ch.ethz.idsc.retina.util.io.PcapParse;
import ch.ethz.idsc.retina.util.io.PcapRealtimePlayback;

enum Hdl32ePcapPanoramaDemo {
  ;
  public static void main(String[] args) throws Exception {
    LidarPanoramaFrame hdl32ePanoramaFrame = new LidarPanoramaFrame();
    Hdl32ePanoramaCollector hdl32ePanoramaCollector = new Hdl32ePanoramaCollector();
    hdl32ePanoramaCollector.addListener(hdl32ePanoramaFrame);
    VelodynePcapPacketListener velodynePcapPacketListener = VelodynePcapPacketListener.hdl32e();
    velodynePcapPacketListener.velodyneDecoder.addRayListener(hdl32ePanoramaCollector);
    velodynePcapPacketListener.velodyneDecoder.addPosListener(hdl32ePanoramaFrame);
    PcapParse.of(Hdl32ePcap.HIGHWAY.file, new PcapRealtimePlayback(1), velodynePcapPacketListener); // blocking
  }
}
