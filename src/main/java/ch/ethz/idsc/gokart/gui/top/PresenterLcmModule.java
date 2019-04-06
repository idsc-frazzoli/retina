// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.core.map.SightLineMapping;
import ch.ethz.idsc.gokart.core.map.SightLines;
import ch.ethz.idsc.gokart.core.map.TrackReconModule;
import ch.ethz.idsc.gokart.core.map.TrackReconRender;
import ch.ethz.idsc.gokart.core.mpc.MPCControlUpdateLcmClient;
import ch.ethz.idsc.gokart.core.perc.ClusterCollection;
import ch.ethz.idsc.gokart.core.perc.ClusterConfig;
import ch.ethz.idsc.gokart.core.perc.LidarClustering;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmLidar;
import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.core.pure.GokartTrajectoryModule;
import ch.ethz.idsc.gokart.core.pure.TrajectoryLcmClient;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.ManualControlLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.GokartStatusLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.LinmotGetLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.RimoGetLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.RimoPutLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.SteerGetLcmClient;
import ch.ethz.idsc.gokart.lcm.davis.DavisLcmClient;
import ch.ethz.idsc.gokart.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Get;
import ch.ethz.idsc.tensor.io.Put;
import ch.ethz.idsc.tensor.io.UserName;

public class PresenterLcmModule extends AbstractModule {
  // TODO not generic
  private static final boolean SHOW_DAVIS = UserName.is("mario");
  // ---
  protected final TimerFrame timerFrame = new TimerFrame();
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private final Vlp16LcmHandler vlp16LcmHandler = SensorsConfig.GLOBAL.vlp16LcmHandler();
  private final RimoGetLcmClient rimoGetLcmClient = new RimoGetLcmClient();
  private final RimoPutLcmClient rimoPutLcmClient = new RimoPutLcmClient();
  private final SteerGetLcmClient steerGetLcmClient = new SteerGetLcmClient();
  private final LinmotGetLcmClient linmotGetLcmClient = new LinmotGetLcmClient();
  private final GokartStatusLcmClient gokartStatusLcmClient = new GokartStatusLcmClient();
  private final ManualControlLcmClient manualControlLcmClient = new ManualControlLcmClient();
  private final List<TrajectoryLcmClient> trajectoryLcmClients = Arrays.asList( //
      TrajectoryLcmClient.xyat(), TrajectoryLcmClient.xyavt());
  private final MPCControlUpdateLcmClient mpcControlUpdateLcmClient = new MPCControlUpdateLcmClient();
  private final GokartPoseLcmLidar gokartPoseLcmLidar = new GokartPoseLcmLidar();
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final PoseTrailRender poseTrailRender = new PoseTrailRender();
  private final DavisLcmClient davisLcmClient = new DavisLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);
  private final MPCPredictionRender lcmMPCPredictionRender = new MPCPredictionRender();
  private final TrackReconRender trackReconRender = new TrackReconRender();
  private final GokartTrajectoryModule gokartTrajectoryModule = //
      ModuleAuto.INSTANCE.getInstance(GokartTrajectoryModule.class);
  private final TrackReconModule gokartTrackReconModule = //
      ModuleAuto.INSTANCE.getInstance(TrackReconModule.class);
  // TODO probably remove again
  private final SightLineMapping sightLineMapping = SightLineMapping.defaultTrack();
  private final SightLines sightLines = SightLines.defaultGokart();

  @Override // from AbstractModule
  protected void first() {
    {
      timerFrame.geometricComponent.addRenderInterface(sightLineMapping);
      sightLineMapping.start();
    }
    {
      timerFrame.geometricComponent.addRenderInterface(sightLines);
      sightLines.start();
    }
    {
      ImageRegion imageRegion = LocalizationConfig.getPredefinedMap().getImageRegion();
      timerFrame.geometricComponent.addRenderInterfaceBackground(RegionRenders.create(imageRegion));
    }
    if (Objects.nonNull(gokartTrajectoryModule))
      timerFrame.geometricComponent.addRenderInterface(gokartTrajectoryModule.obstacleMapping());
    {
      if (Objects.nonNull(gokartTrackReconModule)) {
        timerFrame.geometricComponent.addRenderInterface(gokartTrackReconModule.trackMapping());
        gokartTrackReconModule.listenersAdd(trackReconRender);
      }
      timerFrame.geometricComponent.addRenderInterface(trackReconRender);
    }
    {
      ExtrudedFootprintRender extrudedFootprintRender = new ExtrudedFootprintRender();
      gokartStatusLcmClient.addListener(extrudedFootprintRender.gokartStatusListener);
      gokartPoseLcmClient.addListener(extrudedFootprintRender.gokartPoseListener);
      timerFrame.geometricComponent.addRenderInterface(extrudedFootprintRender);
    }
    {
      gokartPoseLcmClient.addListener(poseTrailRender);
      timerFrame.geometricComponent.addRenderInterface(poseTrailRender);
    }
    // ---
    {
      ParallelLidarRender lidarRender = new ParallelLidarRender(gokartPoseLcmLidar);
      lidarRender.setReference(() -> SensorsConfig.GLOBAL.vlp16);
      lidarRender.setColor(new Color(0, 0, 128, 128));
      lidarRender.setObstacleColor(new Color(128, 0, 128, 128));
      lidarRender.pointSize = 1;
      vlp16LcmHandler.lidarAngularFiringCollector.addListener(lidarRender);
      timerFrame.geometricComponent.addRenderInterface(lidarRender);
    }
    {
      ClusterCollection collection = new ClusterCollection();
      LidarClustering lidarClustering = new LidarClustering(ClusterConfig.GLOBAL, collection, gokartPoseLcmLidar);
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
      // final Tensor waypoints = TrajectoryConfig.getWaypoints();
      // RenderInterface waypointRender = new Se2WaypointRender(waypoints, Arrowhead.of(0.6), new Color(64, 192, 64, 128));
      // timerFrame.geometricComponent.addRenderInterface(waypointRender);
    }
    {
      TrigonometryRender trigonometryRender = new TrigonometryRender();
      gokartPoseLcmClient.addListener(trigonometryRender.gokartPoseListener);
      gokartStatusLcmClient.addListener(trigonometryRender.gokartStatusListener);
      timerFrame.geometricComponent.addRenderInterface(trigonometryRender);
    }
    {
      Vlp16ClearanceRender vlp16ClearanceRender = new Vlp16ClearanceRender(gokartPoseLcmLidar);
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
      GokartRender gokartRender = new GokartRender();
      // joystickLcmClient.addListener(gokartRender.joystickListener);
      rimoGetLcmClient.addListener(gokartRender.rimoGetListener);
      rimoPutLcmClient.addListener(gokartRender.rimoPutListener);
      linmotGetLcmClient.addListener(gokartRender.linmotGetListener);
      gokartStatusLcmClient.addListener(gokartRender.gokartStatusListener);
      gokartPoseLcmClient.addListener(gokartRender.gokartPoseListener);
      timerFrame.geometricComponent.addRenderInterface(gokartRender);
    }
    timerFrame.geometricComponent.addRenderInterface(Dubilab.GRID_RENDER);
    {
      JButton jButton = new JButton("show matrix");
      jButton.addActionListener(actionEvent -> {
        Tensor model2Pixel = timerFrame.geometricComponent.getModel2Pixel();
        System.out.println("model2Pixel=\n" + model2Pixel);
      });
      timerFrame.jToolBar.add(jButton);
    }
    if (SHOW_DAVIS) {
      {
        AccumulatedEventRender accumulatedEventRender = new AccumulatedEventRender(gokartPoseLcmLidar);
        davisLcmClient.addDvsListener(accumulatedEventRender.abstractAccumulatedImage);
        timerFrame.geometricComponent.addRenderInterface(accumulatedEventRender);
        timerFrame.jToolBar.add(accumulatedEventRender.jToggleButton);
      }
      {
        DavisPipelineRender davisPipelineRenderRender = new DavisPipelineRender(gokartPoseLcmLidar);
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
      mpcControlUpdateLcmClient.addListener(lcmMPCPredictionRender);
      timerFrame.geometricComponent.addRenderInterface(lcmMPCPredictionRender);
    }
    {
      GokartHudRender gokartHudRender = new GokartHudRender(gokartPoseLcmLidar);
      steerGetLcmClient.addListener(gokartHudRender.steerGetListener);
      timerFrame.geometricComponent.addRenderInterface(gokartHudRender);
      rimoGetLcmClient.addListener(gokartHudRender.rimoGetListener);
    }
    // ---
    gokartPoseLcmLidar.gokartPoseLcmClient.startSubscriptions();
    gokartPoseLcmClient.startSubscriptions();
    rimoGetLcmClient.startSubscriptions();
    rimoPutLcmClient.startSubscriptions();
    linmotGetLcmClient.startSubscriptions();
    steerGetLcmClient.startSubscriptions();
    gokartStatusLcmClient.startSubscriptions();
    manualControlLcmClient.startSubscriptions();
    vlp16LcmHandler.startSubscriptions();
    trajectoryLcmClients.forEach(TrajectoryLcmClient::startSubscriptions);
    davisLcmClient.startSubscriptions();
    mpcControlUpdateLcmClient.startSubscriptions();
    // ---
    windowConfiguration.attach(getClass(), timerFrame.jFrame);
    timerFrame.configCoordinateOffset(400, 500);
    final File file = AppCustomization.file(getClass(), "model2pixel.tensor");
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
    if (Objects.nonNull(gokartTrackReconModule))
      gokartTrackReconModule.listenersRemove(trackReconRender);
  }

  private void private_windowClosed() {
    gokartPoseLcmClient.stopSubscriptions();
    rimoGetLcmClient.stopSubscriptions();
    rimoPutLcmClient.stopSubscriptions();
    linmotGetLcmClient.stopSubscriptions();
    steerGetLcmClient.stopSubscriptions();
    gokartStatusLcmClient.stopSubscriptions();
    gokartPoseLcmLidar.gokartPoseLcmClient.stopSubscriptions();
    manualControlLcmClient.stopSubscriptions();
    vlp16LcmHandler.stopSubscriptions();
    trajectoryLcmClients.forEach(TrajectoryLcmClient::stopSubscriptions);
    davisLcmClient.stopSubscriptions();
    // sightLines.stop();
    // sightLineMapping.stop();
    mpcControlUpdateLcmClient.stopSubscriptions();
  }

  public static void main(String[] args) throws Exception {
    PresenterLcmModule globalViewLcmModule = new PresenterLcmModule();
    globalViewLcmModule.first();
    globalViewLcmModule.timerFrame.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }
}
