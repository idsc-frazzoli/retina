// code by jph
package ch.ethz.idsc.gokart.offline.video;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.gokart.core.mpc.ControlAndPredictionSteps;
import ch.ethz.idsc.gokart.core.mpc.ControlAndPredictionStepsMessage;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pure.TrajectoryEvents;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.gokart.gui.top.AccelerationRender;
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
import ch.ethz.idsc.gokart.offline.channel.Vmu931ImuChannel;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.TrajectoryRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class TrackVideoRender implements OfflineLogListener, RenderInterface {
  private final MPCPredictionSequenceRender mpcPredictionSequenceRender = new MPCPredictionSequenceRender(20);
  private final MPCPredictionRender mpcPredictionRender = new MPCPredictionRender();
  private final DriftLinesRender driftLinesRender = new DriftLinesRender(100);
  private final GokartRender gokartRender = new GlobalGokartRender();
  private final AccelerationRender accelerationRender;
  private final GroundSpeedRender groundSpeedRender;
  private final TachometerMustangDash tachometerMustangDash;
  private final TrajectoryRender trajectoryRender = new TrajectoryRender();
  private final ExtrudedFootprintRender extrudedFootprintRender = new ExtrudedFootprintRender();
  private final String poseChannel;
  // ---
  private LinmotGetEvent linmotGetEvent;

  public TrackVideoRender(Tensor model2pixel, String poseChannel) {
    this.poseChannel = poseChannel;
    accelerationRender = new AccelerationRender(50, //
        Inverse.of(model2pixel) //
            .dot(Se2Utils.toSE2Matrix(Tensors.vector(960 + 250, 140, 0))) //
            .dot(Se2Utils.toSE2Matrix(Tensors.vector(0, 0, -Math.PI / 2))) //
            .dot(DiagonalMatrix.of(8, -8, 1)));
    Tensor matrix = Inverse.of(model2pixel) //
        .dot(Se2Utils.toSE2Matrix(Tensors.vector(960 - 250, 140, 0))) //
        .dot(Se2Utils.toSE2Matrix(Tensors.vector(0, 0, -Math.PI / 2))) //
        .dot(DiagonalMatrix.of(10, -10, 1));
    groundSpeedRender = new GroundSpeedRender(50, matrix);
    tachometerMustangDash = new TachometerMustangDash(matrix); //
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.STATUS)) {
      GokartStatusEvent gokartStatusEvent = new GokartStatusEvent(byteBuffer);
      gokartRender.gokartStatusListener.getEvent(gokartStatusEvent);
      extrudedFootprintRender.gokartStatusListener.getEvent(gokartStatusEvent);
    } else //
    if (channel.equals(LinmotLcmServer.CHANNEL_GET)) {
      linmotGetEvent = new LinmotGetEvent(byteBuffer);
      gokartRender.linmotGetListener.getEvent(linmotGetEvent);
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
      driftLinesRender.getEvent(gokartPoseEvent);
      groundSpeedRender.getEvent(gokartPoseEvent);
      gokartRender.gokartPoseListener.getEvent(gokartPoseEvent);
      extrudedFootprintRender.gokartPoseListener.getEvent(gokartPoseEvent);
    }
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    mpcPredictionSequenceRender.render(geometricLayer, graphics);
    mpcPredictionRender.render(geometricLayer, graphics);
    driftLinesRender.render(geometricLayer, graphics);
    gokartRender.render(geometricLayer, graphics);
    trajectoryRender.render(geometricLayer, graphics);
    extrudedFootprintRender.render(geometricLayer, graphics);
    accelerationRender.render(geometricLayer, graphics);
    groundSpeedRender.render(geometricLayer, graphics);
    tachometerMustangDash.render(geometricLayer, graphics);
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
