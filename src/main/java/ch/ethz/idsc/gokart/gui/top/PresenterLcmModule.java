// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.core.map.GokartMappingModule;
import ch.ethz.idsc.gokart.core.perc.ClusterCollection;
import ch.ethz.idsc.gokart.core.perc.ClusterConfig;
import ch.ethz.idsc.gokart.core.perc.LidarClustering;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmLidar;
import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.core.pure.TrajectoryConfig;
import ch.ethz.idsc.gokart.core.pure.TrajectoryLcmClient;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.autobox.GokartStatusLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.LinmotGetLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.RimoGetLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.RimoPutLcmClient;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.Se2WaypointRender;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.owl.math.planar.Arrowhead;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.retina.lcm.davis.DavisLcmClient;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClient;
import ch.ethz.idsc.retina.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.sys.AppCustomization;
import ch.ethz.idsc.retina.util.gui.WindowConfiguration;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Get;
import ch.ethz.idsc.tensor.io.Put;

public class PresenterLcmModule extends AbstractModule {
  private static final VehicleModel VEHICLE_MODEL = RimoSinusIonModel.standard();
  // TODO not generic
  private static final boolean SHOW_DAVIS = UserHome.file("").getName().equals("mario");
  // ---
  protected final TimerFrame timerFrame = new TimerFrame();
  private final Vlp16LcmHandler vlp16LcmHandler = SensorsConfig.GLOBAL.vlp16LcmHandler();
  private final RimoGetLcmClient rimoGetLcmClient = new RimoGetLcmClient();
  private final RimoPutLcmClient rimoPutLcmClient = new RimoPutLcmClient();
  private final LinmotGetLcmClient linmotGetLcmClient = new LinmotGetLcmClient();
  private final GokartStatusLcmClient gokartStatusLcmClient = new GokartStatusLcmClient();
  private final JoystickLcmClient joystickLcmClient = new JoystickLcmClient(GokartLcmChannel.JOYSTICK);
  private final List<TrajectoryLcmClient> trajectoryLcmClients = Arrays.asList( //
      TrajectoryLcmClient.xyat(), TrajectoryLcmClient.xyavt());
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private final GokartPoseLcmLidar gokartPoseInterface = new GokartPoseLcmLidar();
  private final DavisLcmClient davisLcmClient = new DavisLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);

  @Override // from AbstractModule
  protected void first() throws Exception {
    {
      ImageRegion imageRegion = LocalizationConfig.getPredefinedMap().getImageRegion();
      timerFrame.geometricComponent.addRenderInterfaceBackground(RegionRenders.create(imageRegion));
    }
    {
      if (Objects.nonNull(GokartMappingModule.GRID_RENDER))
        timerFrame.geometricComponent.addRenderInterface(GokartMappingModule.GRID_RENDER);
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
      lidarRender.pointSize = 1;
      vlp16LcmHandler.lidarAngularFiringCollector.addListener(lidarRender);
      timerFrame.geometricComponent.addRenderInterface(lidarRender);
    }
    {
      ClusterCollection collection = new ClusterCollection();
      LidarClustering lidarClustering = new LidarClustering(ClusterConfig.GLOBAL, collection, gokartPoseInterface);
      ObstacleClusterTrackingRender obstacleClusterTrackingRender = //
          new ObstacleClusterTrackingRender(lidarClustering);
      vlp16LcmHandler.lidarAngularFiringCollector.addListener(lidarClustering);
      timerFrame.geometricComponent.addRenderInterface(obstacleClusterTrackingRender);
      timerFrame.jToolBar.add(obstacleClusterTrackingRender.jToggleButton);
    }
    // {
    // CurveRender curveRender = new CurveRender(DubendorfCurve.HYPERLOOP_DUCTTAPE);
    // timerFrame.geometricComponent.addRenderInterface(curveRender);
    // }
    {
      final Tensor waypoints = TrajectoryConfig.getWaypoints();
      RenderInterface waypointRender = new Se2WaypointRender(waypoints, Arrowhead.of(0.6), new Color(64, 192, 64, 128));
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
      JButton jButton = new JButton("show matrix");
      jButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          Tensor model2Pixel = timerFrame.geometricComponent.getModel2Pixel();
          System.out.println("model2Pixel=\n" + model2Pixel);
        }
      });
      timerFrame.jToolBar.add(jButton);
    }
    if (SHOW_DAVIS) {
      {
        AccumulatedEventRender accumulatedEventRender = new AccumulatedEventRender(gokartPoseInterface);
        davisLcmClient.addDvsListener(accumulatedEventRender.abstractAccumulatedImage);
        timerFrame.geometricComponent.addRenderInterface(accumulatedEventRender);
        timerFrame.jToolBar.add(accumulatedEventRender.jToggleButton);
      }
      {
        DavisPipelineRender davisPipelineRenderRender = new DavisPipelineRender(gokartPoseInterface);
        davisLcmClient.addDvsListener(davisPipelineRenderRender.pipelineProvider);
        timerFrame.geometricComponent.addRenderInterface(davisPipelineRenderRender);
        timerFrame.jToolBar.add(davisPipelineRenderRender.jToggleButton);
      }
    }
    {
      TrajectoryRender trajectoryRender = new TrajectoryRender();
      trajectoryLcmClients.forEach(trajectoryLcmClient -> trajectoryLcmClient.addListener(trajectoryRender));
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
    trajectoryLcmClients.forEach(TrajectoryLcmClient::startSubscriptions);
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
    trajectoryLcmClients.forEach(TrajectoryLcmClient::stopSubscriptions);
    davisLcmClient.stopSubscriptions();
  }

  public static void main(String[] args) throws Exception {
    PresenterLcmModule globalViewLcmModule = new PresenterLcmModule();
    globalViewLcmModule.first();
    globalViewLcmModule.timerFrame.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }
}
