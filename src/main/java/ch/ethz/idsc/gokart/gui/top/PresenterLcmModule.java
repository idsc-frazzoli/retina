// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;

import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmLidar;
import ch.ethz.idsc.gokart.lcm.autobox.GokartStatusLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.LinmotGetLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.RimoGetLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.RimoPutLcmClient;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.retina.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.sys.AppCustomization;
import ch.ethz.idsc.retina.util.gui.WindowConfiguration;

class PresenterLcmModule extends AbstractModule {
  private static final VehicleModel VEHICLE_MODEL = RimoSinusIonModel.standard();
  private static final boolean SHOW_OBSTACLES = true;
  // ---
  protected final TimerFrame timerFrame = new TimerFrame();
  private final Vlp16LcmHandler vlp16LcmHandler = SensorsConfig.GLOBAL.vlp16LcmHandler();
  private final RimoGetLcmClient rimoGetLcmClient = new RimoGetLcmClient();
  private final RimoPutLcmClient rimoPutLcmClient = new RimoPutLcmClient();
  private final LinmotGetLcmClient linmotGetLcmClient = new LinmotGetLcmClient();
  private final GokartStatusLcmClient gokartStatusLcmClient = new GokartStatusLcmClient();
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private final GokartPoseLcmLidar gokartPoseInterface = new GokartPoseLcmLidar();

  public PresenterLcmModule() {
  }

  @Override // from AbstractModule
  protected void first() throws Exception {
    {
      ImageRegion imageRegion = PredefinedMap.DUBENDORF_HANGAR_20180122.getImageRegion();
      timerFrame.geometricComponent.addRenderInterfaceBackground(RegionRenders.create(imageRegion));
    }
    {
      PathRender pathRender = new PathRender(gokartPoseInterface);
      gokartStatusLcmClient.addListener(pathRender.gokartStatusListener);
      timerFrame.geometricComponent.addRenderInterface(pathRender);
    }
    // ---
    {
      LidarRender lidarRender = new ParallelLidarRender(gokartPoseInterface);
      lidarRender.setReference(() -> SensorsConfig.GLOBAL.vlp16);
      lidarRender.setColor(new Color(0, 0, 128, 128));
      lidarRender.pointSize = 1;
      vlp16LcmHandler.lidarAngularFiringCollector.addListener(lidarRender);
      timerFrame.geometricComponent.addRenderInterface(lidarRender);
    }
    if (SHOW_OBSTACLES) { // set to true in order to visualize obstacles
      LidarRender lidarRender = new ObstacleLidarRenderClustering(gokartPoseInterface);
      lidarRender.setReference(() -> SensorsConfig.GLOBAL.vlp16);
      lidarRender.setColor(new Color(255, 0, 0, 128));
      lidarRender.pointSize = 4;
      vlp16LcmHandler.lidarAngularFiringCollector.addListener(lidarRender);
      timerFrame.geometricComponent.addRenderInterface(lidarRender);
    }
    {
      timerFrame.geometricComponent.addRenderInterface(new CurveRender());
    }
    // {
    // LidarRender lidarRender = new PerspectiveLidarRender(() -> SensorsConfig.GLOBAL.vlp16);
    // // lidarRender.setColor(new Color(128, 0, 0, 255));
    // vlp16LcmHandler.lidarAngularFiringCollector.addListener(lidarRender);
    // timerFrame.geometricComponent.addRenderInterface(lidarRender);
    // }
    {
      GokartRender gokartRender = new GokartRender(gokartPoseInterface, VEHICLE_MODEL);
      rimoGetLcmClient.addListener(gokartRender.rimoGetListener);
      rimoPutLcmClient.addListener(gokartRender.rimoPutListener);
      linmotGetLcmClient.addListener(gokartRender.linmotGetListener);
      gokartStatusLcmClient.addListener(gokartRender.gokartStatusListener);
      timerFrame.geometricComponent.addRenderInterface(gokartRender);
    }
    timerFrame.geometricComponent.addRenderInterface(GridRender.INSTANCE);
    {
      OdometryHudRender hudOdometryRender = new OdometryHudRender();
      timerFrame.geometricComponent.addRenderInterface(hudOdometryRender);
      rimoGetLcmClient.addListener(hudOdometryRender);
    }
    // ---
    gokartPoseInterface.gokartPoseLcmClient.startSubscriptions();
    rimoGetLcmClient.startSubscriptions();
    rimoPutLcmClient.startSubscriptions();
    linmotGetLcmClient.startSubscriptions();
    gokartStatusLcmClient.startSubscriptions();
    vlp16LcmHandler.startSubscriptions();
    // ---
    windowConfiguration.attach(getClass(), timerFrame.jFrame);
    timerFrame.configCoordinateOffset(400, 500);
    timerFrame.jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    timerFrame.jFrame.setVisible(true);
  }

  @Override // from AbstractModule
  protected void last() {
    rimoGetLcmClient.stopSubscriptions();
    rimoPutLcmClient.stopSubscriptions();
    linmotGetLcmClient.stopSubscriptions();
    gokartStatusLcmClient.stopSubscriptions();
    gokartPoseInterface.gokartPoseLcmClient.stopSubscriptions();
    // ---
    vlp16LcmHandler.stopSubscriptions();
    timerFrame.close();
  }

  public static void main(String[] args) throws Exception {
    PresenterLcmModule globalViewLcmModule = new PresenterLcmModule();
    globalViewLcmModule.first();
    globalViewLcmModule.timerFrame.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }
}
