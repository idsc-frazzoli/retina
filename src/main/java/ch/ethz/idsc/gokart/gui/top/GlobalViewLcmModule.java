// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.core.map.TrackReconRender;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.core.pos.MappedPoseInterface;
import ch.ethz.idsc.gokart.core.pure.TrajectoryLcmClient;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
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

public class GlobalViewLcmModule extends AbstractModule {
  private static final VehicleModel VEHICLE_MODEL = RimoSinusIonModel.standard();
  private static final Tensor CROP_REGION = ResourceData.of( //
      "/dubilab/polygonregion/aerotain/20190309.csv" //
  // "/dubilab/polygonregion/walkable/20190307.csv" //
  );
  // ---
  private final ViewLcmFrame viewLcmFrame = new ViewLcmFrame();
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
  public final TrackReconRender trackReconRender = new TrackReconRender();

  /** @param curve may be null */
  public void setCurve(Tensor curve) {
    pathRender.setCurve(curve, true);
  }

  /** @param waypoints may be null */
  public void setWaypoints(Tensor waypoints) {
    waypointRender.setWaypoints(waypoints);
  }

  @Override // from AbstractModule
  public void first() {
    final MappedPoseInterface mappedPoseInterface = viewLcmFrame.mappedPoseInterface();
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
    viewLcmFrame.geometricComponent.addRenderInterface(trackReconRender);
    viewLcmFrame.geometricComponent.addRenderInterface(MPCPredictionRender.INSTANCE);
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
    viewLcmFrame.geometricComponent.addRenderInterface(Dubilab.GRID_RENDER);
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
  public void last() {
    viewLcmFrame.close();
    // if (Objects.nonNull(trackReconModule))
    // trackReconModule.listenersRemove(trackReconRender);
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

  public static void standalone() throws Exception {
    ModuleAuto.INSTANCE.runOne(LidarLocalizationModule.class);
    GlobalViewLcmModule globalViewLcmModule = new GlobalViewLcmModule();
    globalViewLcmModule.first();
    globalViewLcmModule.viewLcmFrame.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    // ---
    globalViewLcmModule.viewLcmFrame.jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        ModuleAuto.INSTANCE.endOne(LidarLocalizationModule.class);
      }
    });
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
