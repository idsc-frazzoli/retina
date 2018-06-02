// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Objects;

import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.core.map.GokartMappingModule;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmLidar;
import ch.ethz.idsc.gokart.core.pure.TrajectoryLcmClient;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.autobox.GokartStatusLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.LinmotGetLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.RimoGetLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.RimoPutLcmClient;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.Se2WaypointRender;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.retina.lcm.davis.DavisLcmClient;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClient;
import ch.ethz.idsc.retina.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.sys.AppCustomization;
import ch.ethz.idsc.retina.util.gui.WindowConfiguration;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Get;
import ch.ethz.idsc.tensor.io.Put;
import ch.ethz.idsc.tensor.io.ResourceData;

public class PresenterLcmModule extends AbstractModule {
  private static final VehicleModel VEHICLE_MODEL = RimoSinusIonModel.standard();
  // ---
  protected final TimerFrame timerFrame = new TimerFrame();
  private final Vlp16LcmHandler vlp16LcmHandler = SensorsConfig.GLOBAL.vlp16LcmHandler();
  private final RimoGetLcmClient rimoGetLcmClient = new RimoGetLcmClient();
  private final RimoPutLcmClient rimoPutLcmClient = new RimoPutLcmClient();
  private final LinmotGetLcmClient linmotGetLcmClient = new LinmotGetLcmClient();
  private final GokartStatusLcmClient gokartStatusLcmClient = new GokartStatusLcmClient();
  private final JoystickLcmClient joystickLcmClient = new JoystickLcmClient(GokartLcmChannel.JOYSTICK);
  private final TrajectoryLcmClient trajectoryLcmClient = new TrajectoryLcmClient();
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private final GokartPoseLcmLidar gokartPoseInterface = new GokartPoseLcmLidar();
  private final DavisLcmClient davisLcmClient = new DavisLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);

  @Override // from AbstractModule
  protected void first() throws Exception {
    vlp16LcmHandler.lidarSpacialProvider.addListener(new LidarSpaceTimeListener());
    {
      ImageRegion imageRegion = PredefinedMap.DUBENDORF_HANGAR_20180506.getImageRegion();
      timerFrame.geometricComponent.addRenderInterfaceBackground(RegionRenders.create(imageRegion));
    }
    {
      if (Objects.nonNull(GokartMappingModule.grid))
        timerFrame.geometricComponent.addRenderInterface(GokartMappingModule.grid);
    }
    {
      PathRender pathRender = new PathRender(gokartPoseInterface);
      gokartStatusLcmClient.addListener(pathRender.gokartStatusListener);
      timerFrame.geometricComponent.addRenderInterface(pathRender);
    }
    // ---
    {
      ParallelLidarRender lidarRender = new ParallelLidarRender(gokartPoseInterface);
      lidarRender.setReference(() -> SensorsConfig.GLOBAL.vlp16);
      lidarRender.setColor(new Color(0, 0, 128, 128));
      lidarRender.pointSize = 2;
      vlp16LcmHandler.lidarAngularFiringCollector.addListener(lidarRender);
      timerFrame.geometricComponent.addRenderInterface(lidarRender);
    }
    // {
    // ObstacleClusterRender obstacleClusterRender = //
    // new ObstacleClusterRender(gokartPoseInterface);
    // obstacleClusterRender.setReference(() -> SensorsConfig.GLOBAL.vlp16);
    // obstacleClusterRender.setColor(new Color(255, 0, 0, 128));
    // obstacleClusterRender.pointSize = 4;
    // vlp16LcmHandler.lidarAngularFiringCollector.addListener(obstacleClusterRender);
    // vlp16LcmHandler.lidarAngularFiringCollector.addListener(obstacleClusterRender.lidarRayBlockListener);
    // timerFrame.geometricComponent.addRenderInterface(obstacleClusterRender);
    // timerFrame.jToolBar.add(obstacleClusterRender.jToggleButton);
    // }
    {
      ObstacleClusterTrackingRender obstacleTimeClusterRender = //
          new ObstacleClusterTrackingRender(gokartPoseInterface);
      obstacleTimeClusterRender.setReference(() -> SensorsConfig.GLOBAL.vlp16);
      obstacleTimeClusterRender.setColor(new Color(255, 0, 0, 128));
      obstacleTimeClusterRender.pointSize = 4;
      vlp16LcmHandler.lidarAngularFiringCollector.addListener(obstacleTimeClusterRender); // TODO
      vlp16LcmHandler.lidarAngularFiringCollector.addListener(obstacleTimeClusterRender.lidarRayBlockListener);
      timerFrame.geometricComponent.addRenderInterface(obstacleTimeClusterRender);
      timerFrame.jToolBar.add(obstacleTimeClusterRender.jToggleButton);
    }
    // {
    // CurveRender curveRender = new CurveRender(DubendorfCurve.HYPERLOOP_DUCTTAPE);
    // timerFrame.geometricComponent.addRenderInterface(curveRender);
    // }
    {
      final Tensor waypoints = ResourceData.of("/demo/dubendorf/hangar/20180425waypoints.csv");
      final Tensor ARROWHEAD = Tensors.matrixDouble( //
          new double[][] { { .3, 0 }, { -.1, -.1 }, { -.1, +.1 } }).multiply(RealScalar.of(2));
      RenderInterface waypointRender = new Se2WaypointRender(waypoints, ARROWHEAD, new Color(64, 192, 64, 128));
      timerFrame.geometricComponent.addRenderInterface(waypointRender);
    }
    {
      TrigonometryRender trigonometryRender = new TrigonometryRender(gokartPoseInterface);
      gokartStatusLcmClient.addListener(trigonometryRender.gokartStatusListener);
      timerFrame.geometricComponent.addRenderInterface(trigonometryRender);
    }
    {
      Vlp16ClearanceRender vlp16ClearanceRender = new Vlp16ClearanceRender(gokartPoseInterface);
      gokartStatusLcmClient.addListener(vlp16ClearanceRender.gokartStatusListener);
      vlp16LcmHandler.lidarAngularFiringCollector.addListener(vlp16ClearanceRender);
      timerFrame.geometricComponent.addRenderInterface(vlp16ClearanceRender);
    }
    // {
    // LidarRender lidarRender = new PerspectiveLidarRender(() -> SensorsConfig.GLOBAL.vlp16);
    // // lidarRender.setColor(new Color(128, 0, 0, 255));
    // vlp16LcmHandler.lidarAngularFiringCollector.addListener(lidarRender);
    // timerFrame.geometricComponent.addRenderInterface(lidarRender);
    // }
    {
      GokartRender gokartRender = new GokartRender(gokartPoseInterface, VEHICLE_MODEL);
      joystickLcmClient.addListener(gokartRender.joystickListener);
      rimoGetLcmClient.addListener(gokartRender.rimoGetListener);
      rimoPutLcmClient.addListener(gokartRender.rimoPutListener);
      linmotGetLcmClient.addListener(gokartRender.linmotGetListener);
      gokartStatusLcmClient.addListener(gokartRender.gokartStatusListener);
      timerFrame.geometricComponent.addRenderInterface(gokartRender);
    }
    timerFrame.geometricComponent.addRenderInterface(GridRender.INSTANCE);
    {
      AccumulatedEventRender accumulatedEventRender = new AccumulatedEventRender(gokartPoseInterface);
      davisLcmClient.davisDvsDatagramDecoder.addDvsListener(accumulatedEventRender.abstractAccumulatedImage);
      timerFrame.geometricComponent.addRenderInterface(accumulatedEventRender);
      timerFrame.jToolBar.add(accumulatedEventRender.jToggleButton);
    }
    {
      DavisPipelineRender davisPipelineRenderRender = new DavisPipelineRender(gokartPoseInterface);
      davisLcmClient.davisDvsDatagramDecoder.addDvsListener(davisPipelineRenderRender.pipelineProvider);
      timerFrame.geometricComponent.addRenderInterface(davisPipelineRenderRender);
      timerFrame.jToolBar.add(davisPipelineRenderRender.jToggleButton);
    }
    {
      TrajectoryRender trajectoryRender = new TrajectoryRender();
      trajectoryLcmClient.addListener(trajectoryRender);
      timerFrame.geometricComponent.addRenderInterface(trajectoryRender);
    }
    {
      GokartHudRender gokartHudRender = new GokartHudRender(gokartPoseInterface);
      joystickLcmClient.addListener(gokartHudRender);
      timerFrame.geometricComponent.addRenderInterface(gokartHudRender);
      rimoGetLcmClient.addListener(gokartHudRender);
    }
    // ---
    gokartPoseInterface.gokartPoseLcmClient.startSubscriptions();
    rimoGetLcmClient.startSubscriptions();
    rimoPutLcmClient.startSubscriptions();
    linmotGetLcmClient.startSubscriptions();
    gokartStatusLcmClient.startSubscriptions();
    joystickLcmClient.startSubscriptions();
    vlp16LcmHandler.startSubscriptions();
    trajectoryLcmClient.startSubscriptions();
    davisLcmClient.startSubscriptions();
    // ---
    windowConfiguration.attach(getClass(), timerFrame.jFrame);
    timerFrame.configCoordinateOffset(400, 500);
    final File file = AppCustomization.file(PresenterLcmModule.class.getSimpleName() + "_model2pixel.tensor");
    try {
      timerFrame.geometricComponent.setModel2Pixel(Get.of(file));
    } catch (Exception exception) {
      // ---
    }
    timerFrame.jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        try {
          Put.of(file, timerFrame.geometricComponent.getModel2Pixel());
        } catch (Exception exception) {
          exception.printStackTrace();
        }
        private_windowClosed();
      }
    });
    timerFrame.jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    timerFrame.jFrame.setVisible(true);
  }

  @Override // from AbstractModule
  protected void last() {
    timerFrame.close();
  }

  private void private_windowClosed() {
    rimoGetLcmClient.stopSubscriptions();
    rimoPutLcmClient.stopSubscriptions();
    linmotGetLcmClient.stopSubscriptions();
    gokartStatusLcmClient.stopSubscriptions();
    gokartPoseInterface.gokartPoseLcmClient.stopSubscriptions();
    joystickLcmClient.stopSubscriptions();
    vlp16LcmHandler.stopSubscriptions();
    trajectoryLcmClient.stopSubscriptions();
    davisLcmClient.stopSubscriptions();
  }

  public static void main(String[] args) throws Exception {
    PresenterLcmModule globalViewLcmModule = new PresenterLcmModule();
    globalViewLcmModule.first();
    globalViewLcmModule.timerFrame.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }
}
