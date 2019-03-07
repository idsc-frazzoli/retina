// code by jph
package ch.ethz.idsc.demo.jph.lidar;

import java.util.function.Supplier;

import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmClient;
import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.app.GrayscaleLidarPanorama;
import ch.ethz.idsc.retina.lidar.app.LidarPanorama;
import ch.ethz.idsc.retina.lidar.app.LidarPanoramaProvider;
import ch.ethz.idsc.retina.lidar.app.VelodyneUtils;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16PanoramaProvider;

/** displays hdl32e live data stream as depth and intensity panorama */
enum Vlp16LcmPanoramaDemo {
  ;
  public static void main(String[] args) throws Exception {
    VelodyneModel velodyneModel = VelodyneModel.VLP16;
    VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
    VelodyneLcmClient velodyneLcmClient = new VelodyneLcmClient(velodyneModel, velodyneDecoder, "center");
    Supplier<LidarPanorama> supplier = () -> new GrayscaleLidarPanorama(2304, 16);
    LidarPanoramaProvider lidarPanoramaProvider = new Vlp16PanoramaProvider(supplier);
    // ---
    VelodyneUtils.panorama(velodyneDecoder, lidarPanoramaProvider);
    velodyneLcmClient.startSubscriptions();
  }
}
