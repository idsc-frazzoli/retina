// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;

import ch.ethz.idsc.gokart.core.ekf.SimplePositionVelocityModule;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.gokart.dev.linmot.LinmotSocket;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerGetListener;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;

/* package */ class SimpleDynamicMPCStateEstimationProvider extends MPCStateEstimationProvider {
  private final SimplePositionVelocityModule simpleVelocityEstimation = //
      ModuleAuto.INSTANCE.getInstance(SimplePositionVelocityModule.class);
  private Scalar Ux = Quantity.of(0, SI.VELOCITY);
  private Scalar Uy = Quantity.of(0, SI.VELOCITY);
  private Scalar orientation = RealScalar.of(0);
  private Scalar dotOrientation = Quantity.of(0, SI.PER_SECOND);
  private Scalar XPosition = Quantity.of(0, SI.METER);
  private Scalar YPosition = Quantity.of(0, SI.METER);
  private Scalar w2L = Quantity.of(0, SI.PER_SECOND);
  private Scalar w2R = Quantity.of(0, SI.PER_SECOND);
  private Scalar s = Quantity.of(0, SteerPutEvent.UNIT_ENCODER);
  private Scalar bTemp = Quantity.of(0, NonSI.DEGREE_CELSIUS);
  private Scalar lastUpdate = Quantity.of(0, SI.SECOND);
  private final boolean centerAtCoM;
  private GokartState lastGokartState = null;
  private final LinmotGetListener linmotGetListener = new LinmotGetListener() {
    @Override
    public void getEvent(LinmotGetEvent getEvent) {
      bTemp = getEvent.getWindingTemperatureMax();
      lastUpdate = getTime();
    }
  };
  private final RimoGetListener rimoGetListener = new RimoGetListener() {
    @Override
    public void getEvent(RimoGetEvent getEvent) {
      w2L = getEvent.getTireL.getAngularRate_Y();
      w2R = getEvent.getTireR.getAngularRate_Y();
      lastUpdate = getTime();
    }
  };
  private final SteerColumnInterface steerColumnInterface = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final SteerGetListener steerGetListener = new SteerGetListener() {
    @Override
    public void getEvent(SteerGetEvent getEvent) {
      if (steerColumnInterface.isSteerColumnCalibrated()) {
        // TODO is this smart? Can we get the info directly from the getEvenet
        s = steerColumnInterface.getSteerColumnEncoderCentered();
        lastUpdate = getTime();
      }
    }
  };
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final GokartPoseListener gokartPoseListener = new GokartPoseListener() {
    @Override
    public void getEvent(GokartPoseEvent getEvent) {
      Tensor pose = getEvent.getPose();
      orientation = pose.Get(2);
      if (!centerAtCoM) {
        XPosition = pose.Get(0);
        YPosition = pose.Get(1);
      } else {
        Tensor shiftedPose = AngleVector.of(orientation).multiply(ChassisGeometry.GLOBAL.xAxleRtoCoM);
        XPosition = pose.Get(0).add(shiftedPose.Get(0));
        YPosition = pose.Get(1).add(shiftedPose.Get(1));
      }
    }
  };

  protected SimpleDynamicMPCStateEstimationProvider(Timing timing) {
    super(timing);
    this.centerAtCoM = false;
  }

  protected SimpleDynamicMPCStateEstimationProvider(Timing timing, boolean centerAtCoM) {
    super(timing);
    this.centerAtCoM = centerAtCoM;
  }

  @Override
  public GokartState getState() {
    // check if there was an update since the creation of the last gokart state
    if (Objects.isNull(lastGokartState) || !lastGokartState.getTime().equals(lastUpdate)) {
      Ux = simpleVelocityEstimation.getVelocity().Get(0);
      Uy = simpleVelocityEstimation.getVelocity().Get(1).add(dotOrientation.multiply(ChassisGeometry.GLOBAL.xAxleRtoCoM));
      dotOrientation = simpleVelocityEstimation.getVelocity().Get(2);
      lastGokartState = new GokartState( //
          getTime(), //
          Ux, //
          Uy, //
          dotOrientation, //
          XPosition, //
          YPosition, //
          orientation, //
          w2L, //
          w2R, //
          s, //
          bTemp);
    }
    return lastGokartState;
  }

  @Override
  void first() {
    LinmotSocket.INSTANCE.addGetListener(linmotGetListener);
    SteerSocket.INSTANCE.addGetListener(steerGetListener);
    RimoSocket.INSTANCE.addGetListener(rimoGetListener);
    gokartPoseLcmClient.addListener(gokartPoseListener);
    gokartPoseLcmClient.startSubscriptions();
  }

  @Override
  void last() {
    LinmotSocket.INSTANCE.removeGetListener(linmotGetListener);
    SteerSocket.INSTANCE.removeGetListener(steerGetListener);
    RimoSocket.INSTANCE.removeGetListener(rimoGetListener);
    gokartPoseLcmClient.stopSubscriptions();
  }
}
