// code by jph
package ch.ethz.idsc.retina.demo.jph.lidar;

import ch.ethz.idsc.retina.dev.lidar.app.LidarPanoramaProvider;
import ch.ethz.idsc.retina.dev.lidar.app.VelodyneUtils;
import ch.ethz.idsc.retina.dev.lidar.hdl32e.Hdl32ePanoramaProvider;
import ch.ethz.idsc.retina.lcm.lidar.VelodyneLcmClient;

/** displays hdl32e live data stream as depth and intensity panorama */
enum Hdl32eLcmPanoramaDemo {
  ;
  public static void main(String[] args) throws Exception {
    VelodyneLcmClient velodyneLcmClient = VelodyneLcmClient.hdl32e("center");
    LidarPanoramaProvider lidarPanoramaProvider = new Hdl32ePanoramaProvider();
    // ---
    VelodyneUtils.panorama(velodyneLcmClient.velodyneDecoder, lidarPanoramaProvider);
    velodyneLcmClient.startSubscriptions();
  }
}
