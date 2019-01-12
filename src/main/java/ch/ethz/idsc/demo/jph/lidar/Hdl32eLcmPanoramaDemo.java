// code by jph
package ch.ethz.idsc.demo.jph.lidar;

import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmClient;
import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.app.LidarPanoramaProvider;
import ch.ethz.idsc.retina.lidar.app.VelodyneUtils;
import ch.ethz.idsc.retina.lidar.hdl32e.Hdl32eDecoder;
import ch.ethz.idsc.retina.lidar.hdl32e.Hdl32ePanoramaProvider;

/** displays hdl32e lcm messages as depth and intensity panorama */
enum Hdl32eLcmPanoramaDemo {
  ;
  public static void main(String[] args) throws Exception {
    VelodyneModel velodyneModel = VelodyneModel.HDL32E;
    VelodyneDecoder velodyneDecoder = new Hdl32eDecoder();
    VelodyneLcmClient velodyneLcmClient = new VelodyneLcmClient(velodyneModel, velodyneDecoder, "center");
    LidarPanoramaProvider lidarPanoramaProvider = new Hdl32ePanoramaProvider();
    // ---
    VelodyneUtils.panorama(velodyneDecoder, lidarPanoramaProvider);
    velodyneLcmClient.startSubscriptions();
  }
}
