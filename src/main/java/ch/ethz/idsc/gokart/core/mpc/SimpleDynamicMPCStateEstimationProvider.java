// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;

import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
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
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ class SimpleDynamicMPCStateEstimationProvider extends MPCStateEstimationProvider {
  private final LidarLocalizationModule lidarLocalizationModule = //
      ModuleAuto.INSTANCE.getInstance(LidarLocalizationModule.class);
  private Scalar w2L = Quantity.of(0, SI.PER_SECOND);
  private Scalar w2R = Quantity.of(0, SI.PER_SECOND);
  private Scalar s = Quantity.of(0, SteerPutEvent.UNIT_ENCODER);
  private Scalar bTemp = Quantity.of(0, NonSI.DEGREE_CELSIUS);
  private Scalar lastUpdate = Quantity.of(0, SI.SECOND);
  private Scalar tau = Quantity.of(0, SteerPutEvent.UNIT_RTORQUE);
  private Scalar uDots = Quantity.of(0, SteerPutEvent.UNIT_ENCODER_DOT);
  private GokartState lastGokartState = null;
  private Scalar kTerm = RealScalar.of(4753);
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
        // TODO is this smart? Can we get the info directly from the getEvent
        s = steerColumnInterface.getSteerColumnEncoderCentered();
        uDots = Quantity.of(getEvent.motAsp().divide(kTerm), SteerPutEvent.UNIT_ENCODER_DOT);
        tau = getEvent.estMotTrq();//Should it use last commanded torque?
        lastUpdate = getTime();
      }
    }
  };

  protected SimpleDynamicMPCStateEstimationProvider(Timing timing) {
    super(timing);
  }

  @Override // from MPCStateEstimationProvider
  public GokartState getState() {
    // check if there was an update since the creation of the last gokart state
    if (Objects.isNull(lastGokartState) || !lastGokartState.getTime().equals(lastUpdate)) {
      Tensor velocity = lidarLocalizationModule.getVelocity();
      Tensor pose = lidarLocalizationModule.getPose();
      lastGokartState = new GokartState( //
          getTime(), //
          velocity.Get(0), //
          velocity.Get(1), //
          velocity.Get(2), //
          pose.Get(0), //
          pose.Get(1), //
          pose.Get(2), //
          w2L, //
          w2R, //
          s, //
          bTemp, //
          tau, //
          uDots //
      );
    }
    return lastGokartState;
  }

  @Override
  void first() {
    LinmotSocket.INSTANCE.addGetListener(linmotGetListener);
    SteerSocket.INSTANCE.addGetListener(steerGetListener);
    RimoSocket.INSTANCE.addGetListener(rimoGetListener);
  }

  @Override
  void last() {
    LinmotSocket.INSTANCE.removeGetListener(linmotGetListener);
    SteerSocket.INSTANCE.removeGetListener(steerGetListener);
    RimoSocket.INSTANCE.removeGetListener(rimoGetListener);
  }
}
