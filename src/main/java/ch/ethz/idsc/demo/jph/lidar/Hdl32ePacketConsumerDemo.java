// code by jph
package ch.ethz.idsc.demo.jph.lidar;

import ch.ethz.idsc.retina.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.lidar.app.LidarPanorama;
import ch.ethz.idsc.retina.lidar.app.LidarPanoramaListener;
import ch.ethz.idsc.retina.lidar.app.VelodynePcapPacketListener;
import ch.ethz.idsc.retina.lidar.hdl32e.Hdl32ePanoramaProvider;
import ch.ethz.idsc.retina.util.io.PcapParse;
import ch.ethz.idsc.retina.util.io.PcapRealtimePlayback;

enum Hdl32ePacketConsumerDemo {
  ;
  public static void main(String[] args) throws Exception {
    @SuppressWarnings("unused")
    LidarRayBlockListener lidarRayBlockListener = new LidarRayBlockListener() {
      @Override
      public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
        System.out.println("here");
      }
    };
    LidarPanoramaListener hdl32ePanoramaListener = new LidarPanoramaListener() {
      @Override
      public void lidarPanorama(LidarPanorama hdl32ePanorama) {
        // System.out.println("here");
      }
    };
    Hdl32ePanoramaProvider hdl32ePanoramaCollector = new Hdl32ePanoramaProvider();
    hdl32ePanoramaCollector.addListener(hdl32ePanoramaListener);
    VelodynePcapPacketListener velodynePcapPacketDecoder = VelodynePcapPacketListener.hdl32e();
    velodynePcapPacketDecoder.velodyneDecoder.addRayListener(hdl32ePanoramaCollector);
    PcapParse.of(Hdl32ePcap.TUNNEL.file, new PcapRealtimePlayback(1), velodynePcapPacketDecoder);
  }
}
