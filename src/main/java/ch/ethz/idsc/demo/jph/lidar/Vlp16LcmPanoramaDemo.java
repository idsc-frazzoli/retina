// code by jph
package ch.ethz.idsc.demo.jph.lidar;

import ch.ethz.idsc.retina.dev.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.dev.lidar.VelodyneModel;
import ch.ethz.idsc.retina.dev.lidar.app.LidarPanoramaProvider;
import ch.ethz.idsc.retina.dev.lidar.app.VelodyneUtils;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16PanoramaProvider;
import ch.ethz.idsc.retina.lcm.lidar.VelodyneLcmClient;

/** displays hdl32e live data stream as depth and intensity panorama */
enum Vlp16LcmPanoramaDemo {
  ;
  public static void main(String[] args) throws Exception {
    VelodyneModel velodyneModel = VelodyneModel.VLP16;
    VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
    VelodyneLcmClient velodyneLcmClient = new VelodyneLcmClient(velodyneModel, velodyneDecoder, "center");
    LidarPanoramaProvider lidarPanoramaProvider = new Vlp16PanoramaProvider();
    // ---
    VelodyneUtils.panorama(velodyneDecoder, lidarPanoramaProvider);
    velodyneLcmClient.startSubscriptions();
  }
}
