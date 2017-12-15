// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.Color;

import javax.swing.WindowConstants;

import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.owly.car.core.VehicleModel;
import ch.ethz.idsc.owly.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.retina.dev.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.app.VelodynePlanarEmulator;
import ch.ethz.idsc.retina.dev.zhkart.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.gui.gokart.GokartLcmChannel;
import ch.ethz.idsc.retina.lcm.autobox.GokartStatusLcmClient;
import ch.ethz.idsc.retina.lcm.autobox.LinmotGetLcmClient;
import ch.ethz.idsc.retina.lcm.autobox.RimoGetLcmClient;
import ch.ethz.idsc.retina.lcm.autobox.RimoPutLcmClient;
import ch.ethz.idsc.retina.lcm.lidar.Urg04lxLcmHandler;
import ch.ethz.idsc.retina.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.sys.AppCustomization;
import ch.ethz.idsc.retina.util.gui.WindowConfiguration;

abstract class ViewLcmModule extends AbstractModule {
  private static final VehicleModel VEHICLE_MODEL = RimoSinusIonModel.standard();
  // ---
  protected final TimerFrame timerFrame = new TimerFrame();
  private final Urg04lxLcmHandler urg04lxLcmHandler = new Urg04lxLcmHandler(GokartLcmChannel.URG04LX_FRONT);
  private final Vlp16LcmHandler vlp16LcmHandler = new Vlp16LcmHandler(GokartLcmChannel.VLP16_CENTER);
  private final RimoGetLcmClient rimoGetLcmClient = new RimoGetLcmClient();
  private final RimoPutLcmClient rimoPutLcmClient = new RimoPutLcmClient();
  private final LinmotGetLcmClient linmotGetLcmClient = new LinmotGetLcmClient();
  private final GokartStatusLcmClient gokartStatusLcmClient = new GokartStatusLcmClient();
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private GokartPoseInterface gokartPoseInterface;

  protected void setGokartPoseInterface(GokartPoseInterface gokartPoseInterface) {
    this.gokartPoseInterface = gokartPoseInterface;
  }

  @Override // from AbstractModule
  protected void first() throws Exception {
    timerFrame.geometricComponent.addRenderInterface(GridRender.INSTANCE);
    {
      TrigonometryRender trigonometryRender = new TrigonometryRender(gokartPoseInterface);
      trigonometryRender.setReference(() -> SensorsConfig.GLOBAL.urg04lx);
      gokartStatusLcmClient.addListener(trigonometryRender.gokartStatusListener);
      urg04lxLcmHandler.lidarAngularFiringCollector.addListener(trigonometryRender);
      timerFrame.geometricComponent.addRenderInterface(trigonometryRender);
    }
    {
      PathRender pathRender = new PathRender(gokartPoseInterface);
      gokartStatusLcmClient.addListener(pathRender.gokartStatusListener);
      timerFrame.geometricComponent.addRenderInterface(pathRender);
    }
    // ---
    if (false) {
      {
        LidarRender lidarRender = new PlanarLidarRender(gokartPoseInterface);
        lidarRender.setReference(() -> SensorsConfig.GLOBAL.urg04lx);
        lidarRender.setColor(new Color(128, 192, 128, 64));
        urg04lxLcmHandler.lidarAngularFiringCollector.addListener(lidarRender);
        timerFrame.geometricComponent.addRenderInterface(lidarRender);
      }
      {
        LidarRender lidarRender = new ParallelLidarRender(gokartPoseInterface);
        lidarRender.setReference(() -> SensorsConfig.GLOBAL.urg04lx);
        lidarRender.setColor(new Color(128, 0, 0, 128));
        urg04lxLcmHandler.lidarAngularFiringCollector.addListener(lidarRender);
        timerFrame.geometricComponent.addRenderInterface(lidarRender);
      }
      // ---
      {
        LidarRender lidarRender = new ParallelLidarRender(gokartPoseInterface);
        lidarRender.setReference(() -> SensorsConfig.GLOBAL.vlp16);
        lidarRender.setColor(new Color(0, 0, 128, 128));
        vlp16LcmHandler.lidarAngularFiringCollector.addListener(lidarRender);
        timerFrame.geometricComponent.addRenderInterface(lidarRender);
      }
    }
    {
      LidarRender lidarRender = new ParallelLidarRender(gokartPoseInterface);
      lidarRender.setPointSize(2);
      lidarRender.setReference(() -> SensorsConfig.GLOBAL.vlp16);
      lidarRender.setColor(new Color(255, 0, 128, 128));
      LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(2304, 2);
      LidarSpacialProvider lidarSpacialProvider = VelodynePlanarEmulator.vlp16();
      lidarSpacialProvider.addListener(lidarAngularFiringCollector);
      LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
      lidarRotationProvider.addListener(lidarAngularFiringCollector);
      lidarAngularFiringCollector.addListener(lidarRender);
      vlp16LcmHandler.velodyneDecoder.addRayListener(lidarSpacialProvider);
      vlp16LcmHandler.velodyneDecoder.addRayListener(lidarRotationProvider);
      timerFrame.geometricComponent.addRenderInterface(lidarRender);
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
    // ---
    rimoGetLcmClient.startSubscriptions();
    rimoPutLcmClient.startSubscriptions();
    linmotGetLcmClient.startSubscriptions();
    gokartStatusLcmClient.startSubscriptions();
    urg04lxLcmHandler.startSubscriptions();
    vlp16LcmHandler.startSubscriptions();
    // ---
    // odometryLcmClient.startSubscriptions();
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
    // ---
    // odometryLcmClient.stopSubscriptions();
    // ---
    vlp16LcmHandler.stopSubscriptions();
    urg04lxLcmHandler.stopSubscriptions();
    timerFrame.close();
  }
}
