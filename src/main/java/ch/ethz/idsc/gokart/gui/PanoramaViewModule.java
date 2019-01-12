// code by jph
package ch.ethz.idsc.gokart.gui;

import java.util.function.Supplier;

import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmClient;
import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.app.FullGrayscaleLidarPanorama;
import ch.ethz.idsc.retina.lidar.app.GrayscaleLidarPanorama;
import ch.ethz.idsc.retina.lidar.app.HueLidarPanorama;
import ch.ethz.idsc.retina.lidar.app.LidarPanorama;
import ch.ethz.idsc.retina.lidar.app.LidarPanoramaFrame;
import ch.ethz.idsc.retina.lidar.app.LidarPanoramaProvider;
import ch.ethz.idsc.retina.lidar.app.SuperGrayscaleLidarPanorama;
import ch.ethz.idsc.retina.lidar.app.VelodyneUtils;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16PanoramaProvider;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;

/** and zoom into sectors */
public class PanoramaViewModule extends AbstractModule {
  private VelodyneLcmClient velodyneLcmClient;
  private LidarPanoramaFrame lidarPanoramaFrame;
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());

  @SuppressWarnings("unused")
  @Override // from AbstractModule
  protected void first() throws Exception {
    VelodyneModel velodyneModel = VelodyneModel.VLP16;
    VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
    velodyneLcmClient = new VelodyneLcmClient(velodyneModel, velodyneDecoder, "center");
    FullGrayscaleLidarPanorama gfp = new FullGrayscaleLidarPanorama(16);
    SuperGrayscaleLidarPanorama sgp = new SuperGrayscaleLidarPanorama(16, 4);
    Supplier<LidarPanorama> supplier1 = () -> gfp;
    Supplier<LidarPanorama> supplier2 = () -> new GrayscaleLidarPanorama(2304, 16);
    Supplier<LidarPanorama> supplier3 = () -> new HueLidarPanorama(2304, 16);
    Supplier<LidarPanorama> supplier4 = () -> sgp;
    LidarPanoramaProvider lidarPanoramaProvider = new Vlp16PanoramaProvider(supplier4);
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
