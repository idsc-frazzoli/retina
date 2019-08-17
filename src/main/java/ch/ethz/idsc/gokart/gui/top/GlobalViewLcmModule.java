// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.calib.SensorsConfig;
import ch.ethz.idsc.gokart.core.mpc.MPCControlUpdateLcmClient;
import ch.ethz.idsc.gokart.core.plan.TrajectoryLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.PoseLcmServerModule;
import ch.ethz.idsc.gokart.core.pure.CurveSe2PursuitLcmClient;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.core.track.MPCBSplineTrackRender;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.autobox.LinmotGetLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.RimoGetLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.RimoPutLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.SteerColumnLcmClient;
import ch.ethz.idsc.gokart.lcm.davis.DavisImuLcmClient;
import ch.ethz.idsc.gokart.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.LaneRender;
import ch.ethz.idsc.owl.gui.ren.WaypointRender;
import ch.ethz.idsc.owl.math.lane.LaneInterface;
import ch.ethz.idsc.retina.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.ref.TensorListener;

public class GlobalViewLcmModule extends AbstractModule {
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
  private final SteerColumnLcmClient steerColumnLcmClient = new SteerColumnLcmClient();
  private final MPCControlUpdateLcmClient mpcControlUpdateLcmClient = new MPCControlUpdateLcmClient();
  private final List<TrajectoryLcmClient> trajectoryLcmClients = Arrays.asList( //
      TrajectoryLcmClient.xyat(), //
      TrajectoryLcmClient.xyavt());
  private final CurveSe2PursuitLcmClient curveSe2PursuitLcmClient = new CurveSe2PursuitLcmClient();
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private final WaypointRender waypointRender = new WaypointRender(Arrowhead.of(0.9), new Color(64, 192, 64, 255));
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final PoseTrailRender poseTrailRender = new PoseTrailRender();
  private final MPCPredictionRender lcmMPCPredictionRender = new MPCPredictionRender();
  public final MPCBSplineTrackRender trackReconRender = new MPCBSplineTrackRender();
  private final PathRender pathRender = new PathRender(Color.YELLOW);
  private final PathRender planRender = new PathRender(Color.MAGENTA);
  private final LaneRender laneRender = new LaneRender(false);

  /** @param curve may be null */
  public void setPlan(Tensor curve) {
    planRender.setCurve(curve, false);
  }

  /** @param waypoints may be null */
  public void setWaypoints(Tensor waypoints) {
    waypointRender.setWaypoints(waypoints);
  }

  /** @param laneInterface may be null */
  public void setLane(LaneInterface laneInterface) {
    laneRender.setLane(laneInterface);
  }

  @Override // from AbstractModule
  public void first() {
    viewLcmFrame.geometricComponent.setButtonDrag(MouseEvent.BUTTON1);
    {
      PredefinedMap predefinedMap = LocalizationConfig.GLOBAL.getPredefinedMap();
      RenderInterface renderInterface = new BufferedImageRender(predefinedMap.getImage());
      viewLcmFrame.geometricComponent.addRenderInterface(renderInterface);
    }
    {
      viewLcmFrame.geometricComponent.addRenderInterface(pathRender);
      viewLcmFrame.geometricComponent.addRenderInterface(planRender);
      viewLcmFrame.geometricComponent.addRenderInterface(waypointRender);
      viewLcmFrame.geometricComponent.addRenderInterface(laneRender);
    }
    {
      ExtrudedFootprintRender extrudedFootprintRender = new ExtrudedFootprintRender();
      extrudedFootprintRender.color = new Color(0, 255, 255, 128);
      gokartPoseLcmClient.addListener(extrudedFootprintRender.gokartPoseListener);
      steerColumnLcmClient.addListener(extrudedFootprintRender.steerColumnListener);
      viewLcmFrame.geometricComponent.addRenderInterface(extrudedFootprintRender);
    }
    // ---
    {
      ResampledLidarRender resampledLidarRender = new ResampledLidarRender();
      resampledLidarRender.updatedMap.setCrop(CROP_REGION);
      viewLcmFrame.jButtonMapCreate.addActionListener(resampledLidarRender.action_mapCreate);
      viewLcmFrame.jButtonMapCreate.setEnabled(false);
      viewLcmFrame.jButtonMapUpdate.addActionListener(resampledLidarRender.action_mapUpdate);
      viewLcmFrame.jButtonMapUpdate.setEnabled(resampledLidarRender.updatedMap.nonEmpty());
      resampledLidarRender.setPointSize(2);
      resampledLidarRender.setReference(() -> PoseHelper.toUnitless(SensorsConfig.GLOBAL.vlp16_pose));
      resampledLidarRender.setColor(new Color(255, 0, 128, 128));
      LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(2304, 2);
      LidarSpacialProvider lidarSpacialProvider = LocalizationConfig.GLOBAL.planarEmulatorVlp16();
      lidarSpacialProvider.addListener(lidarAngularFiringCollector);
      LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
      lidarRotationProvider.addListener(lidarAngularFiringCollector);
      lidarAngularFiringCollector.addListener(resampledLidarRender);
      gokartPoseLcmClient.addListener(resampledLidarRender.gokartPoseListener);
      vlp16LcmHandler.velodyneDecoder.addRayListener(lidarSpacialProvider);
      vlp16LcmHandler.velodyneDecoder.addRayListener(lidarRotationProvider);
      viewLcmFrame.geometricComponent.addRenderInterface(resampledLidarRender);
    }
    viewLcmFrame.geometricComponent.addRenderInterface(trackReconRender);
    {
      mpcControlUpdateLcmClient.addListener(lcmMPCPredictionRender);
      viewLcmFrame.geometricComponent.addRenderInterface(lcmMPCPredictionRender);
    }
    {
      TensorListener tensorListener = new TensorListener() {
        @Override
        public void tensorReceived(Tensor tensor) {
          pathRender.setCurve(tensor, true);
        }
      };
      curveSe2PursuitLcmClient.addListener(tensorListener);
      curveSe2PursuitLcmClient.startSubscriptions();
    }
    {
      TrajectoryRender trajectoryRender = new TrajectoryRender();
      trajectoryLcmClients.forEach(trajectoryLcmClient -> trajectoryLcmClient.addListener(trajectoryRender));
      viewLcmFrame.geometricComponent.addRenderInterface(trajectoryRender);
    }
    {
      GokartRender gokartRender = new GlobalGokartRender();
      rimoGetLcmClient.addListener(gokartRender.rimoGetListener);
      rimoPutLcmClient.addListener(gokartRender.rimoPutListener);
      linmotGetLcmClient.addListener(gokartRender.linmotGetListener);
      steerColumnLcmClient.addListener(gokartRender.steerColumnListener);
      gokartPoseLcmClient.addListener(gokartRender.gokartPoseListener);
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
    steerColumnLcmClient.startSubscriptions();
    vlp16LcmHandler.startSubscriptions();
    davisImuLcmClient.startSubscriptions();
    trajectoryLcmClients.forEach(TrajectoryLcmClient::startSubscriptions);
    gokartPoseLcmClient.startSubscriptions();
    mpcControlUpdateLcmClient.startSubscriptions();
    // ---
    windowConfiguration.attach(getClass(), viewLcmFrame.jFrame);
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
  }

  private void private_windowClosed() {
    rimoGetLcmClient.stopSubscriptions();
    rimoPutLcmClient.stopSubscriptions();
    linmotGetLcmClient.stopSubscriptions();
    steerColumnLcmClient.stopSubscriptions();
    curveSe2PursuitLcmClient.stopSubscriptions();
    // ---
    vlp16LcmHandler.stopSubscriptions();
    davisImuLcmClient.stopSubscriptions();
    trajectoryLcmClients.forEach(TrajectoryLcmClient::stopSubscriptions);
    gokartPoseLcmClient.stopSubscriptions();
    mpcControlUpdateLcmClient.stopSubscriptions();
    // ---
    viewLcmFrame.close();
  }

  public static void standalone() throws Exception {
    ModuleAuto.INSTANCE.runOne(LidarLocalizationModule.class);
    ModuleAuto.INSTANCE.runOne(PoseLcmServerModule.class);
    GlobalViewLcmModule globalViewLcmModule = new GlobalViewLcmModule();
    globalViewLcmModule.first();
    globalViewLcmModule.viewLcmFrame.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    // ---
    globalViewLcmModule.viewLcmFrame.jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        ModuleAuto.INSTANCE.endOne(PoseLcmServerModule.class);
        ModuleAuto.INSTANCE.endOne(LidarLocalizationModule.class);
      }
    });
  }

  public static void main(String[] args) throws Exception {
    LocalizationConfig.GLOBAL.predefinedMap = PredefinedMap.DUBILAB_LOCALIZATION_20190708.name();
    standalone();
  }
}
