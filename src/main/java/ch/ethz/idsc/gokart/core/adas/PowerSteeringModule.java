// code by am, jph
package ch.ethz.idsc.gokart.core.adas;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerGetListener;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutProvider;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.tensor.Tensor;

/* package */ abstract class PowerSteeringModule extends AbstractModule implements SteerGetListener, SteerPutProvider {
  private final SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final GokartPoseListener gokartPoseListener = gokartPoseEvent -> this.gokartPoseEvent = gokartPoseEvent;
  private final PowerSteering powerSteering;
  // ---
  private GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();
  private SteerGetEvent steerGetEvent;

  /* package */ PowerSteeringModule(PowerSteering powerSteering) {
    this.powerSteering = powerSteering;
  }

  @Override // from AbstractModule
  protected final void first() {
    gokartPoseLcmClient.addListener(gokartPoseListener);
    gokartPoseLcmClient.startSubscriptions();
    SteerSocket.INSTANCE.addGetListener(this);
    SteerSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  protected final void last() {
    gokartPoseLcmClient.stopSubscriptions();
    SteerSocket.INSTANCE.removeGetListener(this);
    SteerSocket.INSTANCE.removePutProvider(this);
  }

  @Override // from SteerGetListener
  public final void getEvent(SteerGetEvent steerGetEvent) {
    this.steerGetEvent = steerGetEvent;
  }

  @Override // from SteerPutProvider
  public final Optional<SteerPutEvent> putEvent() {
    Tensor velocity = LocalizationConfig.GLOBAL.isQualityOk(gokartPoseEvent) //
        ? gokartPoseEvent.getVelocity()
        : GokartPoseEvents.motionlessUninitialized().getVelocity();
    return steerColumnTracker.isCalibratedAndHealthy() && Objects.nonNull(steerGetEvent) //
        ? Optional.of(SteerPutEvent.createOn(powerSteering.torque( //
            steerColumnTracker.getSteerColumnEncoderCentered(), velocity, steerGetEvent.tsuTrq()))) //
        : Optional.empty();
  }

  @Override // from SteerPutProvider
  public final ProviderRank getProviderRank() {
    return ProviderRank.MANUAL;
  }
}
