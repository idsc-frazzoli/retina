// code by am
package ch.ethz.idsc.gokart.core.adas;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerGetListener;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutProvider;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.ref.ToString;
import ch.ethz.idsc.tensor.sca.Clip;

/** class is used to develop and test anti lock brake logic */
public class LaneKeepingLimitedSteeringModule extends LaneKeepingCenterlineModule implements SteerPutProvider {
  private final SteerColumnInterface steerColumnInterface = SteerSocket.INSTANCE.getSteerColumnTracker();
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
    if (HapticSteerConfig.GLOBAL.printLaneInfo)
      System.out.println("currAngle: " + currAngle);
    if (optional.isPresent()) {
      Clip permittedRange = optional.get();
      if (HapticSteerConfig.GLOBAL.printLaneInfo)
        System.out.println("permittedRange: " + ToString.of(permittedRange));
      final Scalar putTorque = HapticSteerConfig.GLOBAL.laneKeeping(currAngle.subtract(permittedRange.apply(currAngle)));
      final Scalar powerSteer = powerSteering.torque(currAngle, velocity, tsu);
      return Optional.of(SteerPutEvent.createOn(putTorque.add(powerSteer)));
    }
    return Optional.empty();
  }
}