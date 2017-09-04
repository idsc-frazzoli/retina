// code by jph
package ch.ethz.idsc.retina.demo.jph.lidar;

import ch.ethz.idsc.retina.dev.lidar.app.LidarPanoramaProvider;
import ch.ethz.idsc.retina.dev.lidar.app.VelodyneUtils;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16PanoramaProvider;
import ch.ethz.idsc.retina.lcm.lidar.VelodyneLcmClient;

/** displays hdl32e live data stream as depth and intensity panorama */
enum Vlp16LcmPanoramaDemo {
  ;
  public static void main(String[] args) throws Exception {
    VelodyneLcmClient velodyneLcmClient = VelodyneLcmClient.vlp16("center");
    LidarPanoramaProvider lidarPanoramaProvider = new Vlp16PanoramaProvider();
    // ---
    VelodyneUtils.panorama(velodyneLcmClient.velodyneDecoder, lidarPanoramaProvider);
    velodyneLcmClient.startSubscriptions();
  }
}
