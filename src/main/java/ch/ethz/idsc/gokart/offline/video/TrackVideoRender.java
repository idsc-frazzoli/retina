// code by jph
package ch.ethz.idsc.gokart.offline.video;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.gokart.calib.steer.SteerColumnEvent;
import ch.ethz.idsc.gokart.core.adas.HapticSteerConfig;
import ch.ethz.idsc.gokart.core.mpc.ControlAndPredictionSteps;
import ch.ethz.idsc.gokart.core.mpc.ControlAndPredictionStepsMessage;
import ch.ethz.idsc.gokart.core.plan.TrajectoryEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pure.ClothoidPlan;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.dev.u3.GokartLabjackFrame;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.AccelerationRender;
import ch.ethz.idsc.gokart.gui.top.AngularSlipRender;
import ch.ethz.idsc.gokart.gui.top.ClothoidPlanRender;
import ch.ethz.idsc.gokart.gui.top.ExtrudedFootprintRender;
import ch.ethz.idsc.gokart.gui.top.GlobalGokartRender;
import ch.ethz.idsc.gokart.gui.top.GokartRender;
import ch.ethz.idsc.gokart.gui.top.GroundSpeedRender;
import ch.ethz.idsc.gokart.gui.top.MPCPredictionRender;
import ch.ethz.idsc.gokart.gui.top.MPCPredictionSequenceRender;
import ch.ethz.idsc.gokart.gui.top.TachometerMustangDash;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.autobox.LinmotLcmServer;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.gokart.lcm.autobox.SteerLcmServer;
import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.gokart.lcm.mod.ClothoidPlanLcm;
import ch.ethz.idsc.gokart.lcm.mod.Se2CurveLcm;
import ch.ethz.idsc.gokart.offline.channel.Vmu931ImuChannel;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.LaneRender;
import ch.ethz.idsc.owl.gui.ren.TrajectoryRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.lane.LaneInterface;
import ch.ethz.idsc.owl.math.lane.StableLane;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class TrackVideoRender implements OfflineLogListener, RenderInterface {
  private static final String CHANNEL_LIDAR = //
      VelodyneLcmChannels.ray(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  private static final Stroke STROKE = //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  private final PathRender pathRender = new PathRender(new Color(128, 128, 0), STROKE);
  private final LaneRender laneRender = new LaneRender();
  private final LidarPointsRender lidarPointsRender;
  private final MPCPredictionSequenceRender mpcPredictionSequenceRender = new MPCPredictionSequenceRender(20);
  private final MPCPredictionRender mpcPredictionRender = new MPCPredictionRender();
  private final DriftLinesRender driftLinesRender = new DriftLinesRender(250);
  private final SlipLinesRender slipLinesRender = new SlipLinesRender(50 + 250);
  private final GokartRender gokartRender = new GlobalGokartRender();
  private final AccelerationRender accelerationRender;
  private final GroundSpeedRender groundSpeedRender;
  private final AngularSlipRender angularSlipRender;
  private final TachometerMustangDash tachometerMustangDash;
  private final TrajectoryRender trajectoryRender = new TrajectoryRender();
  private final ExtrudedFootprintRender extrudedFootprintRender = new ExtrudedFootprintRender();
  private final Se2ExpFixpointRender se2ExpFixpointRender = new Se2ExpFixpointRender();
  private final AccumulatedImageRender accumulatedImageRender = new AccumulatedImageRender();
  /** set to true when the first event package is registered */
  private boolean hasDavis240c = false;
  private final ClothoidPlansRender clothoidPlansRender = new ClothoidPlansRender(5);
  private final ClothoidPlanRender clothoidPlanRender = new ClothoidPlanRender(Color.MAGENTA);
  private final String poseChannel;
  // ---
  private LinmotGetEvent linmotGetEvent;

  public TrackVideoRender(Tensor model2pixel, String poseChannel) {
    this.poseChannel = poseChannel;
    lidarPointsRender = new LidarPointsRender(model2pixel, 30_000);
    accelerationRender = new AccelerationRender(50, //
        Inverse.of(model2pixel) //
            .dot(Se2Matrix.of(Tensors.vector(960 + 250, 140, 0))) //
            .dot(Se2Matrix.of(Tensors.vector(0, 0, -Math.PI / 2))) //
            .dot(DiagonalMatrix.of(8, -8, 1)));
    Tensor matrix = Inverse.of(model2pixel) //
        .dot(Se2Matrix.of(Tensors.vector(960 - 250, 140, 0))) //
        .dot(Se2Matrix.of(Tensors.vector(0, 0, -Math.PI / 2))) //
        .dot(DiagonalMatrix.of(10, -10, 1));
    groundSpeedRender = new GroundSpeedRender(50, matrix);
    angularSlipRender = new AngularSlipRender(matrix);
    tachometerMustangDash = new TachometerMustangDash(matrix); //
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(CHANNEL_LIDAR)) {
      lidarPointsRender.velodyneDecoder.lasers(byteBuffer);
    } else //
    if (channel.equals(GokartLcmChannel.STATUS)) {
      SteerColumnEvent steerColumnEvent = new SteerColumnEvent(byteBuffer);
      gokartRender.steerColumnListener.getEvent(steerColumnEvent);
      angularSlipRender.steerColumnListener.getEvent(steerColumnEvent);
      slipLinesRender.steerColumnListener.getEvent(steerColumnEvent);
      extrudedFootprintRender.steerColumnListener.getEvent(steerColumnEvent);
    } else //
    if (channel.equals(LinmotLcmServer.CHANNEL_GET)) {
      linmotGetEvent = new LinmotGetEvent(byteBuffer);
      gokartRender.linmotGetListener.getEvent(linmotGetEvent);
    } else //
    if (channel.equals(SteerLcmServer.CHANNEL_GET)) {
      SteerGetEvent steerGetEvent = new SteerGetEvent(byteBuffer);
      gokartRender.steerGetListener.getEvent(steerGetEvent);
    } else //
    if (channel.equals(RimoLcmServer.CHANNEL_GET)) {
      RimoGetEvent rimoGetEvent = new RimoGetEvent(byteBuffer);
      gokartRender.rimoGetListener.getEvent(rimoGetEvent);
      tachometerMustangDash.getEvent(rimoGetEvent);
    } else //
    if (channel.equals(RimoLcmServer.CHANNEL_PUT)) {
      RimoPutEvent rimoGetEvent = RimoPutHelper.from(byteBuffer);
      gokartRender.rimoPutListener.putEvent(rimoGetEvent);
    } else //
    if (channel.equals(Vmu931ImuChannel.INSTANCE.channel())) {
      Vmu931ImuFrame vmu931ImuFrame = new Vmu931ImuFrame(byteBuffer);
      accelerationRender.vmu931ImuFrame(vmu931ImuFrame);
    } else //
    if (channel.equals(GokartLcmChannel.MPC_FORCES_CNS)) {
      ControlAndPredictionSteps controlAndPredictionSteps = new ControlAndPredictionStepsMessage(byteBuffer).getPayload();
      mpcPredictionSequenceRender.getControlAndPredictionSteps(controlAndPredictionSteps);
      mpcPredictionRender.getControlAndPredictionSteps(controlAndPredictionSteps);
    } else //
    if (channel.equals(GokartLcmChannel.TRAJECTORY_XYAT_STATETIME))
      trajectoryRender.trajectory(TrajectoryEvents.trajectory(byteBuffer));
    else //
    if (channel.equals(poseChannel)) {
      GokartPoseEvent gokartPoseEvent = GokartPoseEvent.of(byteBuffer);
      lidarPointsRender.getEvent(gokartPoseEvent);
      driftLinesRender.getEvent(gokartPoseEvent);
      slipLinesRender.getEvent(gokartPoseEvent);
      groundSpeedRender.getEvent(gokartPoseEvent);
      angularSlipRender.gokartPoseListener.getEvent(gokartPoseEvent);
      gokartRender.gokartPoseListener.getEvent(gokartPoseEvent);
      extrudedFootprintRender.gokartPoseListener.getEvent(gokartPoseEvent);
      se2ExpFixpointRender.getEvent(gokartPoseEvent);
    } else //
    if (channel.equals("davis240c.overview.dvs")) { // TODO JPH
      hasDavis240c = true;
      accumulatedImageRender.davisDvsDatagramDecoder.decode(byteBuffer);
    } else //
    if (channel.equals(GokartLcmChannel.LABJACK_U3_ADC)) {
      ManualControlInterface manualControlInterface = new GokartLabjackFrame(byteBuffer);
      gokartRender.manualControlListener.manualControl(manualControlInterface);
    } else //
    if (channel.equals(GokartLcmChannel.PURSUIT_CURVE_SE2)) {
      Tensor tensor = Se2CurveLcm.decode(byteBuffer).unmodifiable();
      pathRender.setCurve(tensor, true);
      LaneInterface laneInterface = //
          StableLane.of(Tensors.empty(), tensor, HapticSteerConfig.GLOBAL.halfWidth);
      laneRender.setLane(laneInterface, true);
    } else //
    if (channel.equals(GokartLcmChannel.PURSUIT_PLAN)) {
      ClothoidPlan clothoidPlan = ClothoidPlanLcm.decode(byteBuffer);
      clothoidPlansRender.planReceived(clothoidPlan);
      clothoidPlanRender.planReceived(clothoidPlan);
    }
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // TODO JPH
    // pathRender.render(geometricLayer, graphics);
    laneRender.render(geometricLayer, graphics);
    lidarPointsRender.render(geometricLayer, graphics);
    if (hasDavis240c)
      accumulatedImageRender.render(geometricLayer, graphics);
    mpcPredictionSequenceRender.render(geometricLayer, graphics);
    mpcPredictionRender.render(geometricLayer, graphics);
    driftLinesRender.render(geometricLayer, graphics);
    slipLinesRender.render(geometricLayer, graphics);
    gokartRender.render(geometricLayer, graphics);
    trajectoryRender.render(geometricLayer, graphics);
    extrudedFootprintRender.render(geometricLayer, graphics);
    accelerationRender.render(geometricLayer, graphics);
    groundSpeedRender.render(geometricLayer, graphics);
    angularSlipRender.render(geometricLayer, graphics);
    tachometerMustangDash.render(geometricLayer, graphics);
    se2ExpFixpointRender.render(geometricLayer, graphics);
    clothoidPlansRender.render(geometricLayer, graphics);
    clothoidPlanRender.render(geometricLayer, graphics);
    // ---
    graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
    graphics.setColor(Color.GRAY);
    if (Objects.nonNull(linmotGetEvent))
      graphics.drawString(String.format("brake:%12s", linmotGetEvent.getWindingTemperatureMax().map(Round._2)), 0, 25 + 30);
    // ---
    graphics.drawString("vel", 960 - 250 - 140, 25);
    graphics.drawString("acc", 960 + 250 - 140, 25);
  }
}
