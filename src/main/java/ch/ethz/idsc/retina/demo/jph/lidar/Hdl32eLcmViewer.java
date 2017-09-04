// code by jph
package ch.ethz.idsc.retina.demo.jph.lidar;

import ch.ethz.idsc.retina.dev.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.dev.lidar.VelodyneModel;
import ch.ethz.idsc.retina.dev.lidar.app.VelodyneUtils;
import ch.ethz.idsc.retina.dev.lidar.hdl32e.Hdl32eDecoder;
import ch.ethz.idsc.retina.dev.lidar.hdl32e.Hdl32ePanoramaProvider;
import ch.ethz.idsc.retina.lcm.lidar.VelodyneLcmClient;

/** simple visualizations of firing and positioning data on lcm for debugging */
public enum Hdl32eLcmViewer {
  ;
  public static void create(String lidarId) {
    VelodyneModel velodyneModel = VelodyneModel.HDL32E;
    VelodyneDecoder velodyneDecoder = new Hdl32eDecoder();
    VelodyneLcmClient velodyneLcmClient = new VelodyneLcmClient(velodyneModel, velodyneDecoder, lidarId);
    // ---
    VelodyneUtils.panorama(velodyneDecoder, new Hdl32ePanoramaProvider());
    VelodyneUtils.createRayFrame( //
        VelodyneUtils.createCollector32(velodyneDecoder), velodyneDecoder);
    // ---
    velodyneLcmClient.startSubscriptions();
  }

  public static void main(String[] args) {
    create("center");
  }
}
