// code by am
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pure.ClothoidPlan;
import ch.ethz.idsc.gokart.core.pure.ClothoidPursuitConfig;
import ch.ethz.idsc.gokart.core.pure.CurveClothoidPursuitPlanner;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutProvider;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/** class is used to develop and test anti lock brake logic */
public class LaneKeepingLimitedSteeringModule extends LaneKeepingCenterlineModule implements SteerPutProvider {
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
  // private final

  @Override // from AbstractModule
  public void first() {
    gokartPoseLcmClient.addListener(this);
    gokartPoseLcmClient.startSubscriptions();
    // SteerSocket.INSTANCE.addGetListener(steerGetListener);
  }

  @Override // from AbstractModule
  public void last() {
    // SteerSocket.INSTANCE.removeGetListener(steerGetListener);
    gokartPoseLcmClient.stopSubscriptions();
  }

  @Override
  public ProviderRank getProviderRank() {
    // TODO JPH
    return ProviderRank.CALIBRATION;
  }

  public Optional<Clip> getPlan(Optional<Tensor> optionalCurve, GokartPoseEvent gokartPoseEvent) {
    Tensor pose = gokartPoseEvent.getPose();
    Tensor curve = optionalCurve.get();
    Scalar steerlimitLSCE = Quantity.of(0, "SCE");
    Scalar steerlimitRSCE = Quantity.of(0, "SCE");
    if (1 < curve.length()) {
      System.out.println("ifloop entered :)");
      ClothoidPursuitConfig clothoidPursuitConfig = new ClothoidPursuitConfig();
      // large value is a hack to get a solution
      clothoidPursuitConfig.turningRatioMax = Quantity.of(1000, SI.PER_METER);
      Optional<ClothoidPlan> optionalL = //
          new CurveClothoidPursuitPlanner(clothoidPursuitConfig).getPlan(pose, Quantity.of(0, SI.VELOCITY), optionalCurve.get(), true);
      Optional<ClothoidPlan> optionalR = //
          new CurveClothoidPursuitPlanner(clothoidPursuitConfig).getPlan(pose, Quantity.of(0, SI.VELOCITY), optionalCurve.get(), true);
      System.out.println(optionalL);
      if (optionalL.isPresent()) {
        Scalar steerlimitLratio = optionalL.get().ratio();
        steerlimitLSCE = steerMapping.getSCEfromRatio(steerlimitLratio);
        System.out.println("Limit L: " + steerlimitLSCE);
      }
      if (optionalR.isPresent()) {
        Scalar steerlimitRratio = optionalR.get().ratio();
        steerlimitRSCE = steerMapping.getSCEfromRatio(steerlimitRratio);
        System.out.println("Limit R: " + steerlimitRSCE);
      }
      // Tensor steerLimitSCE = Tensors.of(steerlimitLSCE, steerlimitRSCE);
      return Optional.of(Clips.interval(steerlimitLSCE, steerlimitRSCE));
    }
    System.out.println("no steer limit found");
    System.out.println("ifloop not entered :(");
    return Optional.empty();
  }

  @Override
  public Optional<SteerPutEvent> putEvent() {
    SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();
    if (steerColumnTracker.isCalibratedAndHealthy()) {
      // TODO AM/JPH getPlan should be called at most every 10[Hz]
      Optional<Clip> steerLimitSCE = getPlan(optionalCurve, gokartPoseEvent);
      Scalar currSteer = steerColumnTracker.getSteerColumnEncoderCentered();
      System.out.println("currSteer: " + currSteer);
      // FIXME not correct: SteerPutEvent is not a position controller
      // if (Scalars.lessThan(steerLimitSCE.Get(0), currSteer)) {
      // System.out.println(steerLimitSCE.Get(0));
      // return Optional.of(SteerPutEvent.createOn(steerLimitSCE.Get(0)));
      // }
      // if (Scalars.lessThan(steerLimitSCE.Get(1), currSteer)) {
      // System.out.println(steerLimitSCE.Get(1));
      // return Optional.of(SteerPutEvent.createOn(steerLimitSCE.Get(1)));
      // }
    }
    return Optional.empty();
  }
}
