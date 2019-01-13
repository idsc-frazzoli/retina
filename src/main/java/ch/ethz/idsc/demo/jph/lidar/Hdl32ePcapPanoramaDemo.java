// code by jph
package ch.ethz.idsc.demo.jph.lidar;

import ch.ethz.idsc.retina.lidar.app.LidarPanoramaFrame;
import ch.ethz.idsc.retina.lidar.app.VelodynePcapPacketListener;
import ch.ethz.idsc.retina.lidar.hdl32e.Hdl32ePanoramaProvider;
import ch.ethz.idsc.retina.util.io.PcapParse;
import ch.ethz.idsc.retina.util.io.PcapRealtimePlayback;

enum Hdl32ePcapPanoramaDemo {
  ;
  public static void main(String[] args) throws Exception {
    LidarPanoramaFrame hdl32ePanoramaFrame = new LidarPanoramaFrame();
    Hdl32ePanoramaProvider hdl32ePanoramaCollector = new Hdl32ePanoramaProvider();
    hdl32ePanoramaCollector.addListener(hdl32ePanoramaFrame);
    VelodynePcapPacketListener velodynePcapPacketListener = VelodynePcapPacketListener.hdl32e();
    velodynePcapPacketListener.velodyneDecoder.addRayListener(hdl32ePanoramaCollector);
    PcapParse.of(Hdl32ePcap.HIGHWAY.file, new PcapRealtimePlayback(1), velodynePcapPacketListener); // blocking
  }
}
