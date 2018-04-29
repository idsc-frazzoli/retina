// code by jph
package ch.ethz.idsc.gokart.gui;

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
import ch.ethz.idsc.retina.sys.AppCustomization;
import ch.ethz.idsc.retina.util.gui.WindowConfiguration;

/** TODO collect information at the full resolution 36000
 * and zoom into sectors
 * TODO ensure that image only takes 16 x N space; rescale only when drawing */
public class PanoramaViewModule extends AbstractModule {
  VelodyneLcmClient velodyneLcmClient;
  LidarPanoramaFrame lidarPanoramaFrame;
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());

  @Override // from AbstractModule
  protected void first() throws Exception {
    VelodyneModel velodyneModel = VelodyneModel.VLP16;
    VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
    velodyneLcmClient = new VelodyneLcmClient(velodyneModel, velodyneDecoder, "center");
    LidarPanoramaProvider lidarPanoramaProvider = new Vlp16PanoramaProvider();
    // ---
    lidarPanoramaFrame = VelodyneUtils.panorama(velodyneDecoder, lidarPanoramaProvider);
    windowConfiguration.attach(getClass(), lidarPanoramaFrame.jFrame);
    lidarPanoramaFrame.jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    lidarPanoramaFrame.jFrame.setVisible(true);
    velodyneLcmClient.startSubscriptions();
  }

  @Override // from AbstractModule
  protected void last() {
    velodyneLcmClient.stopSubscriptions();
    lidarPanoramaFrame.close();
  }

  public static void standalone() throws Exception {
    PanoramaViewModule panoramaViewModule = new PanoramaViewModule();
    panoramaViewModule.first();
    panoramaViewModule.lidarPanoramaFrame.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
