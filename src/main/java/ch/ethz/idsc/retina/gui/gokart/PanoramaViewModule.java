package ch.ethz.idsc.retina.gui.gokart;

import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.dev.lidar.VelodyneModel;
import ch.ethz.idsc.retina.dev.lidar.app.LidarPanoramaFrame;
import ch.ethz.idsc.retina.dev.lidar.app.LidarPanoramaProvider;
import ch.ethz.idsc.retina.dev.lidar.app.VelodyneUtils;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16PanoramaProvider;
import ch.ethz.idsc.retina.lcm.lidar.VelodyneLcmClient;
import ch.ethz.idsc.retina.sys.AbstractModule;

public class PanoramaViewModule extends AbstractModule {
  VelodyneLcmClient velodyneLcmClient;
  LidarPanoramaFrame panoramaFrame;

  @Override
  protected void first() throws Exception {
    // TODO Auto-generated method stub
    VelodyneModel velodyneModel = VelodyneModel.VLP16;
    VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
    velodyneLcmClient = new VelodyneLcmClient(velodyneModel, velodyneDecoder, "center");
    LidarPanoramaProvider lidarPanoramaProvider = new Vlp16PanoramaProvider();
    // ---
    panoramaFrame = VelodyneUtils.panorama(velodyneDecoder, lidarPanoramaProvider);
    panoramaFrame.jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    velodyneLcmClient.startSubscriptions();
  }

  @Override
  protected void last() {
    velodyneLcmClient.stopSubscriptions();
    panoramaFrame.close();
  }
}
