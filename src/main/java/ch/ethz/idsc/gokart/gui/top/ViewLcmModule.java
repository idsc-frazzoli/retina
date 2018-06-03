// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.core.pos.MappedPoseInterface;
import ch.ethz.idsc.gokart.core.pure.DubendorfCurve;
import ch.ethz.idsc.gokart.core.pure.TrajectoryLcmClient;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.autobox.GokartStatusLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.LinmotGetLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.RimoGetLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.RimoPutLcmClient;
import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.Se2WaypointRender;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.retina.dev.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.lcm.davis.DavisImuLcmClient;
import ch.ethz.idsc.retina.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.sys.AppCustomization;
import ch.ethz.idsc.retina.util.gui.WindowConfiguration;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ResourceData;

abstract class ViewLcmModule extends AbstractModule {
  private static final VehicleModel VEHICLE_MODEL = RimoSinusIonModel.standard();
  public static List<TrajectorySample> plannedTrajectory;
  // ---
  protected final ViewLcmFrame viewLcmFrame = new ViewLcmFrame();
  // private final Urg04lxLcmHandler urg04lxLcmHandler = new Urg04lxLcmHandler(GokartLcmChannel.URG04LX_FRONT);
  private final Vlp16LcmHandler vlp16LcmHandler = SensorsConfig.GLOBAL.vlp16LcmHandler();
  private final DavisImuLcmClient davisImuLcmClient = new DavisImuLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);
  private final RimoGetLcmClient rimoGetLcmClient = new RimoGetLcmClient();
  private final RimoPutLcmClient rimoPutLcmClient = new RimoPutLcmClient();
  private final LinmotGetLcmClient linmotGetLcmClient = new LinmotGetLcmClient();
  private final GokartStatusLcmClient gokartStatusLcmClient = new GokartStatusLcmClient();
  private final TrajectoryLcmClient trajectoryLcmClient = new TrajectoryLcmClient();
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private MappedPoseInterface mappedPoseInterface;

  protected void setGokartPoseInterface(MappedPoseInterface mappedPoseInterface) {
    this.mappedPoseInterface = mappedPoseInterface;
    viewLcmFrame.setGokartPoseInterface(mappedPoseInterface);
  }

  @Override // from AbstractModule
  protected void first() throws Exception {
    {
      TrigonometryRender trigonometryRender = new TrigonometryRender(mappedPoseInterface);
      // trigonometryRender.setReference(() -> SensorsConfig.GLOBAL.vlp16);
      gokartStatusLcmClient.addListener(trigonometryRender.gokartStatusListener);
      // vlp16LcmHandler.lidarAngularFiringCollector.addListener(trigonometryRender);
      viewLcmFrame.geometricComponent.addRenderInterface(trigonometryRender);
    }
    {
      PathRender pathRender = new PathRender(mappedPoseInterface);
      gokartStatusLcmClient.addListener(pathRender.gokartStatusListener);
      viewLcmFrame.geometricComponent.addRenderInterface(pathRender);
    }
    // ---
    if (true) {
      // {
      // LidarRender lidarRender = new PlanarLidarRender(gokartPoseInterface);
      // lidarRender.setReference(() -> SensorsConfig.GLOBAL.urg04lx);
      // lidarRender.setColor(new Color(128, 192, 128, 64));
      // urg04lxLcmHandler.lidarAngularFiringCollector.addListener(lidarRender);
      // viewLcmFrame.geometricComponent.addRenderInterface(lidarRender);
      // }
      // {
      // LidarRender lidarRender = new ParallelLidarRender(gokartPoseInterface);
      // lidarRender.setReference(() -> SensorsConfig.GLOBAL.urg04lx);
      // lidarRender.setColor(new Color(128, 0, 0, 128));
      // urg04lxLcmHandler.lidarAngularFiringCollector.addListener(lidarRender);
      // viewLcmFrame.geometricComponent.addRenderInterface(lidarRender);
      // }
      // ---
      {
        LidarRender lidarRender = new ParallelLidarRender(mappedPoseInterface);
        lidarRender.setReference(() -> SensorsConfig.GLOBAL.vlp16);
        lidarRender.setColor(new Color(0, 0, 128, 128));
        vlp16LcmHandler.lidarAngularFiringCollector.addListener(lidarRender);
        viewLcmFrame.geometricComponent.addRenderInterface(lidarRender);
      }
    }
    {
      ResampledLidarRender resampledLidarRender = new ResampledLidarRender(mappedPoseInterface);
      viewLcmFrame.jButtonMapCreate.addActionListener(resampledLidarRender.action_mapCreate);
      viewLcmFrame.jButtonMapCreate.setEnabled(false);
      viewLcmFrame.jButtonMapUpdate.addActionListener(resampledLidarRender.action_mapUpdate);
      viewLcmFrame.jButtonMapUpdate.setEnabled(false);
      viewLcmFrame.jButtonSnap.addActionListener(resampledLidarRender.action_snap);
      // resampledLidarRender.trackSupplier = () -> viewLcmFrame.jToggleButton.isSelected();
      resampledLidarRender.setPointSize(2);
      resampledLidarRender.setReference(() -> SensorsConfig.GLOBAL.vlp16);
      resampledLidarRender.setColor(new Color(255, 0, 128, 128));
      LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(2304, 2);
      // LidarSpacialProvider lidarSpacialProvider = SensorsConfig.GLOBAL.planarEmulatorVlp16_p01deg();
      LidarSpacialProvider lidarSpacialProvider = LocalizationConfig.GLOBAL.planarEmulatorVlp16();
      lidarSpacialProvider.addListener(lidarAngularFiringCollector);
      LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
      lidarRotationProvider.addListener(lidarAngularFiringCollector);
      lidarAngularFiringCollector.addListener(resampledLidarRender);
      // vlp16LcmHandler.lidarAngularFiringCollector.addListener(lidarRender.lrbl);
      vlp16LcmHandler.velodyneDecoder.addRayListener(lidarSpacialProvider);
      vlp16LcmHandler.velodyneDecoder.addRayListener(lidarRotationProvider);
      viewLcmFrame.geometricComponent.addRenderInterface(resampledLidarRender);
    }
    { // TODO not generic
      CurveRender curveRender = new CurveRender(DubendorfCurve.HYPERLOOP_DUCTTAPE);
      viewLcmFrame.geometricComponent.addRenderInterface(curveRender);
    }
    {
      final Tensor waypoints = ResourceData.of("/demo/dubendorf/hangar/20180425waypoints.csv");
      final Tensor ARROWHEAD = Tensors.matrixDouble( //
          new double[][] { { .3, 0 }, { -.1, -.1 }, { -.1, +.1 } }).multiply(RealScalar.of(3));
      RenderInterface waypointRender = new Se2WaypointRender(waypoints, ARROWHEAD, new Color(64, 192, 64, 255));
      viewLcmFrame.geometricComponent.addRenderInterface(waypointRender);
    }
    {
      TrajectoryRender trajectoryRender = new TrajectoryRender();
      trajectoryLcmClient.addListener(trajectoryRender);
      viewLcmFrame.geometricComponent.addRenderInterface(trajectoryRender);
    }
    // {
    // CurveRender curveRender = new CurveRender(FigureOvalModule.CURVE);
    // viewLcmFrame.geometricComponent.addRenderInterface(curveRender);
    // }
    // {
    // LidarRender lidarRender = new PerspectiveLidarRender(() -> SensorsConfig.GLOBAL.vlp16);
    // // lidarRender.setColor(new Color(128, 0, 0, 255));
    // vlp16LcmHandler.lidarAngularFiringCollector.addListener(lidarRender);
    // timerFrame.geometricComponent.addRenderInterface(lidarRender);
    // }
    {
      GokartRender gokartRender = new GokartRender(mappedPoseInterface, VEHICLE_MODEL);
      rimoGetLcmClient.addListener(gokartRender.rimoGetListener);
      rimoPutLcmClient.addListener(gokartRender.rimoPutListener);
      linmotGetLcmClient.addListener(gokartRender.linmotGetListener);
      gokartStatusLcmClient.addListener(gokartRender.gokartStatusListener);
      viewLcmFrame.geometricComponent.addRenderInterface(gokartRender);
    }
    viewLcmFrame.geometricComponent.addRenderInterface(GridRender.INSTANCE);
    // ---
    rimoGetLcmClient.startSubscriptions();
    rimoPutLcmClient.startSubscriptions();
    linmotGetLcmClient.startSubscriptions();
    gokartStatusLcmClient.startSubscriptions();
    // urg04lxLcmHandler.startSubscriptions();
    vlp16LcmHandler.startSubscriptions();
    davisImuLcmClient.startSubscriptions();
    trajectoryLcmClient.startSubscriptions();
    // ---
    // odometryLcmClient.startSubscriptions();
    // ---
    windowConfiguration.attach(getClass(), viewLcmFrame.jFrame);
    viewLcmFrame.configCoordinateOffset(400, 500);
    viewLcmFrame.jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        private_windowClosed();
      }
    });
    viewLcmFrame.jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    viewLcmFrame.jFrame.setVisible(true);
  }

  @Override // from AbstractModule
  protected void last() {
    viewLcmFrame.close();
  }

  private void private_windowClosed() {
    rimoGetLcmClient.stopSubscriptions();
    rimoPutLcmClient.stopSubscriptions();
    linmotGetLcmClient.stopSubscriptions();
    gokartStatusLcmClient.stopSubscriptions();
    // ---
    // odometryLcmClient.stopSubscriptions();
    // ---
    vlp16LcmHandler.stopSubscriptions();
    // urg04lxLcmHandler.stopSubscriptions();
    davisImuLcmClient.stopSubscriptions();
    trajectoryLcmClient.stopSubscriptions();
    viewLcmFrame.close();
  }
}
