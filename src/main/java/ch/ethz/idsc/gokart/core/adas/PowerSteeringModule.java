// code by am, jph
package ch.ethz.idsc.gokart.core.adas;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.RimoAxleConfiguration;
import ch.ethz.idsc.gokart.calib.steer.SteerFeedForwardConfig;
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
import ch.ethz.idsc.owl.car.core.AxleConfiguration;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.sophus.filter.ga.GeodesicIIR1Filter;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Round;

public class PowerSteeringModule extends AbstractModule implements SteerGetListener, SteerPutProvider {
  private final SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final GokartPoseListener gokartPoseListener = gokartPoseEvent -> this.gokartPoseEvent = gokartPoseEvent;
  private final HapticSteerConfig hapticSteerConfig;
  private final GeodesicIIR1Filter geodesicIIR1Filter; // 1 means unfiltered
  // ---
  private GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();
  private SteerGetEvent steerGetEvent;

  public PowerSteeringModule() {
    this(HapticSteerConfig.GLOBAL);
  }

  /* package */ PowerSteeringModule(HapticSteerConfig hapticSteerConfig) {
    this.hapticSteerConfig = hapticSteerConfig;
    geodesicIIR1Filter = new GeodesicIIR1Filter(RnGeodesic.INSTANCE, hapticSteerConfig.velocityFilter);
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
  public void getEvent(SteerGetEvent steerGetEvent) {
    this.steerGetEvent = steerGetEvent;
  }

  @Override // from SteerPutProvider
  public Optional<SteerPutEvent> putEvent() {
    Tensor velocity = LocalizationConfig.GLOBAL.isQualityOk(gokartPoseEvent) //
        ? gokartPoseEvent.getVelocity()
        : GokartPoseEvents.motionlessUninitialized().getVelocity();
    return steerColumnTracker.isCalibratedAndHealthy() && Objects.nonNull(steerGetEvent) //
        ? Optional.of(SteerPutEvent.createOn(putEvent( //
            steerColumnTracker.getSteerColumnEncoderCentered(), velocity, steerGetEvent.tsuTrq()))) //
        : Optional.empty();
  }

  /** @param currangle with unit "SCE"
   * @param velocity {vx[m*s^-1], vy[m*s^-1], omega[s^-1]}
   * @param diffRelRckPos
   * @return scalar with unit SCT */
  /* package */ Scalar putEvent(Scalar currangle, Tensor velocity, Scalar tsu) {
    // term0 is the static compensation of the restoring force, depending on the current angle
    // term1 is the compensation depending on the velocity of the steering wheel
    // term2 amplifies the torque exerted by the driver
    Scalar feedForwardValue = SteerFeedForwardConfig.GLOBAL.series().apply(currangle);
    Scalar term0 = hapticSteerConfig.feedForward //
        ? feedForwardValue
        : feedForwardValue.zero();
    // ---
    AxleConfiguration axleConfiguration = RimoAxleConfiguration.frontFromSCE(currangle);
    Tensor filteredVel = geodesicIIR1Filter.apply(velocity);
    Scalar latFront_LeftVel = axleConfiguration.wheel(0).adjoint(filteredVel).Get(1);
    Scalar latFrontRightVel = axleConfiguration.wheel(1).adjoint(filteredVel).Get(1);
    Scalar term1 = hapticSteerConfig.latForceCompensationBoundaryClip().apply( //
        latFront_LeftVel.add(latFrontRightVel).multiply(hapticSteerConfig.latForceCompensation));
    // ---
    Scalar term2 = tsu.multiply(hapticSteerConfig.tsuFactor);
    if (hapticSteerConfig.printPower)
      System.out.println(Tensors.of(term0, term1, term2).map(Round._3));
    return term0.add(term1).add(term2);
  }

  @Override // from SteerPutProvider
  public final ProviderRank getProviderRank() {
    return ProviderRank.MANUAL;
  }
}
