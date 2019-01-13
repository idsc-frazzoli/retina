// code by jph
package ch.ethz.idsc.demo.jph.lidar;

import ch.ethz.idsc.gokart.lcm.lidar.Mark8LcmClient;
import ch.ethz.idsc.retina.lidar.app.LidarPanoramaProvider;
import ch.ethz.idsc.retina.lidar.app.VelodyneUtils;
import ch.ethz.idsc.retina.lidar.mark8.Mark8PanoramaProvider;

/** displays hdl32e live data stream as depth and intensity panorama */
enum Mark8LcmPanoramaDemo {
  ;
  public static void main(String[] args) throws Exception {
    Mark8LcmClient lcmClientInterface = new Mark8LcmClient("center");
    LidarPanoramaProvider lidarPanoramaProvider = new Mark8PanoramaProvider();
    // ---
    VelodyneUtils.panorama(lcmClientInterface.mark8Decoder, lidarPanoramaProvider);
    lcmClientInterface.startSubscriptions();
  }
}
