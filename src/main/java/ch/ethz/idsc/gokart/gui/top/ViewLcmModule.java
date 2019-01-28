// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.core.map.GokartTrackReconModule;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.core.pos.MappedPoseInterface;
import ch.ethz.idsc.gokart.core.pure.TrajectoryLcmClient;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.autobox.GokartStatusLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.LinmotGetLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.RimoGetLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.RimoPutLcmClient;
import ch.ethz.idsc.gokart.lcm.davis.DavisImuLcmClient;
import ch.ethz.idsc.gokart.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.GridRender;
import ch.ethz.idsc.owl.gui.ren.WaypointRender;
import ch.ethz.idsc.retina.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.planar.Arrowhead;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ResourceData;

abstract class ViewLcmModule extends AbstractModule {
  private static final VehicleModel VEHICLE_MODEL = RimoSinusIonModel.standard();
  private static final Tensor CROP_REGION = //
      ResourceData.of( //
          // "/dubilab/polygonregion/aerotain/20180813.csv" //
          "/dubilab/polygonregion/tents/20180603.csv" //
      );
  // ---
  protected final ViewLcmFrame viewLcmFrame = new ViewLcmFrame();
  private final Vlp16LcmHandler vlp16LcmHandler = SensorsConfig.GLOBAL.vlp16LcmHandler();
  private final DavisImuLcmClient davisImuLcmClient = new DavisImuLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);
  private final RimoGetLcmClient rimoGetLcmClient = new RimoGetLcmClient();
  private final RimoPutLcmClient rimoPutLcmClient = new RimoPutLcmClient();
  private final LinmotGetLcmClient linmotGetLcmClient = new LinmotGetLcmClient();
  private final GokartStatusLcmClient gokartStatusLcmClient = new GokartStatusLcmClient();
  private final List<TrajectoryLcmClient> trajectoryLcmClients = Arrays.asList( //
      TrajectoryLcmClient.xyat(), //
      TrajectoryLcmClient.xyavt());
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private final PathRender pathRender = new PathRender(Color.YELLOW);
  private final WaypointRender waypointRender = new WaypointRender(Arrowhead.of(0.9), new Color(64, 192, 64, 255));
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final PoseTrailRender poseTrailRender = new PoseTrailRender();
  // ---
  private MappedPoseInterface mappedPoseInterface;

  protected void setGokartPoseInterface(MappedPoseInterface mappedPoseInterface) {
    this.mappedPoseInterface = mappedPoseInterface;
    viewLcmFrame.setGokartPoseInterface(mappedPoseInterface);
  }

  /** @param curve may be null */
  public void setCurve(Tensor curve) {
    pathRender.setCurve(curve, true);
  }

  /** @param waypoints may be null */
  public void setWaypoints(Tensor waypoints) {
    waypointRender.setWaypoints(waypoints);
  }

  @Override // from AbstractModule
  protected void first() throws Exception {
    viewLcmFrame.geometricComponent.setButtonDrag(MouseEvent.BUTTON1);
    {
      PredefinedMap predefinedMap = LocalizationConfig.getPredefinedMap();
      RenderInterface renderInterface = //
          new BufferedImageRender(predefinedMap.getImage());
      viewLcmFrame.geometricComponent.addRenderInterface(renderInterface);
    }
    {
      viewLcmFrame.geometricComponent.addRenderInterface(pathRender);
      viewLcmFrame.geometricComponent.addRenderInterface(waypointRender);
    }
    {
      GokartPathRender gokartPathRender = new GokartPathRender(mappedPoseInterface);
      gokartStatusLcmClient.addListener(gokartPathRender.gokartStatusListener);
      viewLcmFrame.geometricComponent.addRenderInterface(gokartPathRender);
    }
    // ---
    {
      ResampledLidarRender resampledLidarRender = new ResampledLidarRender(mappedPoseInterface);
      resampledLidarRender.updatedMap.setCrop(CROP_REGION);
      viewLcmFrame.jButtonMapCreate.addActionListener(resampledLidarRender.action_mapCreate);
      viewLcmFrame.jButtonMapCreate.setEnabled(false);
      viewLcmFrame.jButtonMapUpdate.addActionListener(resampledLidarRender.action_mapUpdate);
      viewLcmFrame.jButtonMapUpdate.setEnabled(resampledLidarRender.updatedMap.nonEmpty());
      viewLcmFrame.jButtonSnap.addActionListener(resampledLidarRender.action_snap);
      // resampledLidarRender.trackSupplier = () -> viewLcmFrame.jToggleButton.isSelected();
      resampledLidarRender.setPointSize(2);
      resampledLidarRender.setReference(() -> SensorsConfig.GLOBAL.vlp16);
      resampledLidarRender.setColor(new Color(255, 0, 128, 128));
      LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(2304, 2);
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
    {
      GokartTrackReconModule gokartTrackReconModule = //
          ModuleAuto.INSTANCE.getInstance(GokartTrackReconModule.class);
      if (Objects.nonNull(gokartTrackReconModule))
        viewLcmFrame.geometricComponent.addRenderInterface(gokartTrackReconModule);
    }
    {
      viewLcmFrame.geometricComponent.addRenderInterface(MPCPredictionRender.INSTANCE);
    }
    {
      TrajectoryRender trajectoryRender = new TrajectoryRender();
      trajectoryLcmClients.forEach(trajectoryLcmClient -> trajectoryLcmClient.addListener(trajectoryRender));
      viewLcmFrame.geometricComponent.addRenderInterface(trajectoryRender);
    }
    {
      GokartRender gokartRender = new GokartRender(mappedPoseInterface, VEHICLE_MODEL);
      rimoGetLcmClient.addListener(gokartRender.rimoGetListener);
      rimoPutLcmClient.addListener(gokartRender.rimoPutListener);
      linmotGetLcmClient.addListener(gokartRender.linmotGetListener);
      gokartStatusLcmClient.addListener(gokartRender.gokartStatusListener);
      viewLcmFrame.geometricComponent.addRenderInterface(gokartRender);
    }
    viewLcmFrame.geometricComponent.addRenderInterface(GridRender.INSTANCE);
    {
      gokartPoseLcmClient.addListener(poseTrailRender);
      viewLcmFrame.geometricComponent.addRenderInterface(poseTrailRender);
    }
    // ---
    rimoGetLcmClient.startSubscriptions();
    rimoPutLcmClient.startSubscriptions();
    linmotGetLcmClient.startSubscriptions();
    gokartStatusLcmClient.startSubscriptions();
    vlp16LcmHandler.startSubscriptions();
    davisImuLcmClient.startSubscriptions();
    trajectoryLcmClients.forEach(TrajectoryLcmClient::startSubscriptions);
    gokartPoseLcmClient.startSubscriptions();
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
    vlp16LcmHandler.stopSubscriptions();
    davisImuLcmClient.stopSubscriptions();
    trajectoryLcmClients.forEach(TrajectoryLcmClient::stopSubscriptions);
    gokartPoseLcmClient.stopSubscriptions();
    // ---
    viewLcmFrame.close();
  }
}
