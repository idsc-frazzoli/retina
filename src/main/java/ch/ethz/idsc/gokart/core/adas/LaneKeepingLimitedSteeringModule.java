// code by am
package ch.ethz.idsc.gokart.core.adas;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerGetListener;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutProvider;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Clip;

/** class is used to develop and test anti lock brake logic */
/* package */ class LaneKeepingLimitedSteeringModule extends LaneKeepingCenterlineModule implements SteerPutProvider {
  private SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();
  private PowerSteeringModule powerSteeringModule = new PowerSteeringModule();
  public SteerGetEvent steerGetEvent;
  public final SteerGetListener steerGetListener = new SteerGetListener() {
    @Override
    public void getEvent(SteerGetEvent getEvent) {
      steerGetEvent = getEvent;
    }
  };

  @Override // from AbstractModule
  public void first() {
    super.first();
    SteerSocket.INSTANCE.addGetListener(steerGetListener);
  }

  @Override // from AbstractModule
  public void last() {
    SteerSocket.INSTANCE.removeGetListener(steerGetListener);
    super.last();
  }

  @Override
  public ProviderRank getProviderRank() {
    // TODO JPH
    return ProviderRank.CALIBRATION;
  }

  @Override
  public Optional<SteerPutEvent> putEvent() {
    if (steerColumnTracker.isCalibratedAndHealthy() && Objects.nonNull(steerGetEvent)) {
      Scalar currAngle = steerColumnTracker.getSteerColumnEncoderCentered();
      Scalar tsu = steerGetEvent.tsuTrq();
      System.out.println("currAngle: " + currAngle);
      if (optionalPermittedRange.isPresent()) {
        Clip permittedRange = optionalPermittedRange.get();
        Scalar putTorque = currAngle.subtract(permittedRange.apply(currAngle)).multiply(HapticSteerConfig.GLOBAL.lanekeepingFactor);
        Scalar powerSteer = powerSteeringModule.putEvent(currAngle, velocity, tsu);
        System.out.println("permittedRange: " + permittedRange);
        return Optional.of(SteerPutEvent.createOn(putTorque.add(powerSteer)));
      }
    }
    return Optional.empty();
  }
}