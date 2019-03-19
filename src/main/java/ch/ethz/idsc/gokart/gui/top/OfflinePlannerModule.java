// code by jph
package ch.ethz.idsc.gokart.gui.top;

import ch.ethz.idsc.gokart.core.map.TrackMapping;
import ch.ethz.idsc.gokart.core.map.TrackReconManagement;
import ch.ethz.idsc.gokart.core.perc.ClusterCollection;
import ch.ethz.idsc.gokart.core.perc.ClusterConfig;
import ch.ethz.idsc.gokart.core.perc.LidarClustering;
import ch.ethz.idsc.gokart.core.pos.*;
import ch.ethz.idsc.gokart.core.pure.GokartTrajectoryModule;
import ch.ethz.idsc.gokart.core.pure.TrajectoryLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.*;
import ch.ethz.idsc.gokart.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.owl.data.IntervalClock;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.*;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Get;
import ch.ethz.idsc.tensor.io.Put;
import ch.ethz.idsc.tensor.qty.Quantity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class OfflinePlannerModule extends AbstractClockedModule implements GokartPoseListener {
  private static final VehicleModel VEHICLE_MODEL = RimoSinusIonModel.standard();
  private static final Scalar PERIOD = Quantity.of(0.5, SI.SECOND);
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
//  private final List<TrajectoryLcmClient> trajectoryLcmClients = Arrays.asList( //
//      TrajectoryLcmClient.xyat(), TrajectoryLcmClient.xyavt());
  private final GokartPoseLcmLidar gokartPoseLcmLidar = new GokartPoseLcmLidar();
  private final PoseTrailRender poseTrailRender = new PoseTrailRender();
  private final GokartTrajectoryModule gokartTrajectoryModule = //
      new GokartTrajectoryModule();
//          ModuleAuto.INSTANCE.getInstance(GokartTrajectoryModule.class);
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final IntervalClock intervalClock = new IntervalClock();
  private final TrackMapping trackMapping = new TrackMapping();
  private final TrackReconManagement trackReconManagement = new TrackReconManagement(trackMapping);

  private GokartPoseEvent gokartPoseEvent = null;
  private boolean activeSubscriptions = false;

  @Override // from AbstractModule
  protected void first() {
    {
      ImageRegion imageRegion = LocalizationConfig.getPredefinedMap().getImageRegion();
      timerFrame.geometricComponent.addRenderInterfaceBackground(RegionRenders.create(imageRegion));
    }
//    if (Objects.nonNull(gokartTrajectoryModule))
//      timerFrame.geometricComponent.addRenderInterface(gokartTrajectoryModule.obstacleMapping());
    {
      gokartPoseLcmClient.addListener(this);
      timerFrame.geometricComponent.addRenderInterface(trackMapping);
      timerFrame.geometricComponent.addRenderInterface(trackReconManagement.getTrackLayoutInitialGuess());
    }
    {
      ExtrudedFootprintRender extrudedFootprintRender = new ExtrudedFootprintRender(gokartPoseLcmLidar);
      gokartStatusLcmClient.addListener(extrudedFootprintRender.gokartStatusListener);
      timerFrame.geometricComponent.addRenderInterface(extrudedFootprintRender);
    }
    {
      gokartPoseLcmLidar.gokartPoseLcmClient.addListener(poseTrailRender);
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
    {
      // final Tensor waypoints = TrajectoryConfig.getWaypoints();
      // RenderInterface waypointRender = new Se2WaypointRender(waypoints, Arrowhead.of(0.6), new Color(64, 192, 64, 128));
      // timerFrame.geometricComponent.addRenderInterface(waypointRender);
    }
    {
      TrigonometryRender trigonometryRender = new TrigonometryRender(gokartPoseLcmLidar);
      gokartStatusLcmClient.addListener(trigonometryRender.gokartStatusListener);
      timerFrame.geometricComponent.addRenderInterface(trigonometryRender);
    }
    {
      Vlp16ClearanceRender vlp16ClearanceRender = new Vlp16ClearanceRender(gokartPoseLcmLidar);
      gokartStatusLcmClient.addListener(vlp16ClearanceRender.gokartStatusListener);
      vlp16LcmHandler.lidarAngularFiringCollector.addListener(vlp16ClearanceRender);
      timerFrame.geometricComponent.addRenderInterface(vlp16ClearanceRender);
    }
    {
      GokartRender gokartRender = new GokartRender(gokartPoseLcmLidar, VEHICLE_MODEL);
      // joystickLcmClient.addListener(gokartRender.joystickListener);
      rimoGetLcmClient.addListener(gokartRender.rimoGetListener);
      rimoPutLcmClient.addListener(gokartRender.rimoPutListener);
      linmotGetLcmClient.addListener(gokartRender.linmotGetListener);
      gokartStatusLcmClient.addListener(gokartRender.gokartStatusListener);
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
    {
      JButton jButton = new JButton("set start");
      jButton.addActionListener(actionEvent -> setStart());
      timerFrame.jToolBar.add(jButton);
    }
    {
      JButton jButton = new JButton("stop");
      // TODO also stop gokartTrajectoryModule
      jButton.addActionListener(actionEvent -> unsubscribe());
      timerFrame.jToolBar.add(jButton);
    }
    {
      JButton jButton = new JButton("continue");
      jButton.addActionListener(actionEvent -> subscribe());
      timerFrame.jToolBar.add(jButton);
    }
    {
      TrajectoryRender trajectoryRender = new TrajectoryRender();
      {
        JButton jButton = new JButton("current trajectory");
        jButton.addActionListener(actionEvent -> //
                // TODO check correct planning
                trajectoryRender.trajectory(gokartTrajectoryModule.currentTrajectory()));
        timerFrame.jToolBar.add(jButton);
      }
//      trajectoryLcmClients.forEach(trajectoryLcmClient -> trajectoryLcmClient.addListener(trajectoryRender));
      timerFrame.geometricComponent.addRenderInterface(trajectoryRender);
    }
    {
      GokartHudRender gokartHudRender = new GokartHudRender(gokartPoseLcmLidar);
      steerGetLcmClient.addListener(gokartHudRender.steerGetListener);
      timerFrame.geometricComponent.addRenderInterface(gokartHudRender);
      rimoGetLcmClient.addListener(gokartHudRender.rimoGetListener);
    }
    // ---
    subscribe();
    trackMapping.start();
    gokartTrajectoryModule.launch();
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

  @Override // from AbstractClockedModule
  protected void runAlgo() {
    double seconds = intervalClock.seconds(); // reset
    if (trackReconManagement.isStartSet()) {
      if (activeSubscriptions) {
        trackMapping.prepareMap();
        trackReconManagement.update(gokartPoseEvent, Quantity.of(seconds, SI.SECOND));
      }
    } else
      System.out.println("no start set");
  }

  @Override // from AbstractClockedModule
  protected Scalar getPeriod() {
    return PERIOD;
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    this.gokartPoseEvent = gokartPoseEvent;
  }

  /** reset track and flag start at current pose */
  public void setStart() {
    if (Objects.isNull(gokartPoseEvent)) {
      System.out.println("no pose");
      return;
    }
    trackReconManagement.setStart(gokartPoseEvent);
  }

  /** reset track */
  public void computeTrack() {
    trackReconManagement.computeTrack();
  }

  @Override // from AbstractModule
  protected void last() {
    timerFrame.close();
  }

  private void private_windowClosed() {
    unsubscribe();
    trackMapping.stop();
    gokartTrajectoryModule.terminate();
    terminate();
  }

  private void subscribe() {
    if (!activeSubscriptions) {
      gokartPoseLcmClient.startSubscriptions();
      gokartPoseLcmLidar.gokartPoseLcmClient.startSubscriptions();
      gokartStatusLcmClient.startSubscriptions();
      linmotGetLcmClient.startSubscriptions();
      rimoGetLcmClient.startSubscriptions();
      rimoPutLcmClient.startSubscriptions();
      steerGetLcmClient.startSubscriptions();
//      trajectoryLcmClients.forEach(TrajectoryLcmClient::startSubscriptions);
      vlp16LcmHandler.startSubscriptions();
      activeSubscriptions = true;
    }
  }

  private void unsubscribe() {
    if (activeSubscriptions) {
      gokartPoseLcmClient.stopSubscriptions();
      gokartPoseLcmLidar.gokartPoseLcmClient.stopSubscriptions();
      gokartStatusLcmClient.stopSubscriptions();
      linmotGetLcmClient.stopSubscriptions();
      rimoGetLcmClient.stopSubscriptions();
      rimoPutLcmClient.stopSubscriptions();
      steerGetLcmClient.stopSubscriptions();
//      trajectoryLcmClients.forEach(TrajectoryLcmClient::stopSubscriptions);
      vlp16LcmHandler.stopSubscriptions();
      activeSubscriptions = false;
    }
  }

  public static void main(String[] args) throws Exception {
    OfflinePlannerModule offlinePlannerModule = new OfflinePlannerModule();
    offlinePlannerModule.launch();
    offlinePlannerModule.timerFrame.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }
}
