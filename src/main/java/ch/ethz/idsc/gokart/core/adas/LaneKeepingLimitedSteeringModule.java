// code by am
package ch.ethz.idsc.gokart.core.adas;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerGetListener;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutProvider;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.gokart.lcm.VectorFloatBlob;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.sophus.lie.se2.Se2ParametricDistance;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Flatten;
import ch.ethz.idsc.tensor.sca.Clip;

/** class is used to develop and test anti lock brake logic */
public class LaneKeepingLimitedSteeringModule extends LaneKeepingCenterlineModule implements SteerPutProvider {
  private final SteerColumnInterface steerColumnInterface = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final BinaryBlobPublisher binaryBlobPublisher = new BinaryBlobPublisher(GokartLcmChannel.LINMOT_ANTILOCK);
  private final PowerSteering powerSteering = new PowerSteering(HapticSteerConfig.GLOBAL);
  // ---
  private SteerGetEvent steerGetEvent;
  private final SteerGetListener steerGetListener = new SteerGetListener() {
    @Override
    public void getEvent(SteerGetEvent getEvent) {
      steerGetEvent = getEvent;
    }
  };

  @Override // from AbstractModule
  public void first() {
    super.first();
    SteerSocket.INSTANCE.addGetListener(steerGetListener);
    SteerSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  public void last() {
    SteerSocket.INSTANCE.removeGetListener(steerGetListener);
    SteerSocket.INSTANCE.removePutProvider(this);
    super.last();
  }

  @Override // from SteerPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.MANUAL;
  }

  @Override // from SteerPutProvider
  public Optional<SteerPutEvent> putEvent() {
    if (steerColumnInterface.isSteerColumnCalibrated() && Objects.nonNull(steerGetEvent))
      return putEvent(steerColumnInterface, steerGetEvent, optionalPermittedRange);
    return Optional.empty();
  }

  Optional<SteerPutEvent> putEvent(SteerColumnInterface steerColumnInterface, SteerGetEvent steerGetEvent, Optional<Clip> optional) {
    Scalar currAngle = steerColumnInterface.getSteerColumnEncoderCentered();
    Scalar tsu = steerGetEvent.tsuTrq();
    if (optionalCurve.isPresent() && LocalizationConfig.GLOBAL.isQualityOk(gokartPoseEvent)) {
      System.out.println("binaryBlob1 entered");
      if (Objects.nonNull(steerGetEvent)) {
        binaryBlobPublisher.accept(VectorFloatBlob.encode(Flatten.of(Tensors.of(//
            closestDistance(optionalCurve.get(), gokartPoseEvent.getPose()), //
            HapticSteerConfig.GLOBAL.offsetL, //
            steerGetEvent.tsuTrq(), //
            velocity))));
        System.out.println("binaryBlob2 entered");
      }
    }
    if (HapticSteerConfig.GLOBAL.printLaneInfo)
      System.out.println("currAngle: " + currAngle);
    if (optional.isPresent()) {
      Clip permittedRange = optional.get();
      if (HapticSteerConfig.GLOBAL.printLaneInfo)
        System.out.println("permittedRange: " + permittedRange);
      final Scalar putTorque = HapticSteerConfig.GLOBAL.laneKeeping(currAngle.subtract(permittedRange.apply(currAngle)));
      System.out.println("putTorque: " + putTorque);
      final Scalar powerSteer = powerSteering.torque(currAngle, velocity, tsu);
      return Optional.of(SteerPutEvent.createOn(putTorque.add(powerSteer)));
    }
    return Optional.empty();
  }

  public Scalar closestDistance(Tensor curve, Tensor pose) {
    int index = Se2CurveHelper.closest(curve, pose); // closest gives the index of the closest element
    Tensor closest = curve.get(index);
    Scalar currDistance = Se2ParametricDistance.INSTANCE.distance(closest, pose);
    return currDistance;
  }
}