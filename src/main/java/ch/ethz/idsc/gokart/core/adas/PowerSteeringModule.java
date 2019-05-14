// code by am and jph
package ch.ethz.idsc.gokart.core.adas;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.RimoAxleConfiguration;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.owl.car.core.AxleConfiguration;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.sophus.filter.GeodesicIIR1Filter;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class PowerSteeringModule extends PowerSteeringBaseModule {
  private final SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final LidarLocalizationModule lidarLocalizationModule = ModuleAuto.INSTANCE.getInstance(LidarLocalizationModule.class);
  private final GeodesicIIR1Filter geodesicIIR1Filter; // 1 means unfiltered
  // ---
  private SteerGetEvent prev;
  private final HapticSteerConfig hapticSteerConfig;
  private double diffRelRckPos;

  public PowerSteeringModule() {
    this(HapticSteerConfig.GLOBAL);
  }

  PowerSteeringModule(HapticSteerConfig hapticSteerConfig) {
    this.hapticSteerConfig = hapticSteerConfig;
    geodesicIIR1Filter = new GeodesicIIR1Filter( //
        RnGeodesic.INSTANCE, hapticSteerConfig.velocityFilter);
  }

  @Override
  public void getEvent(SteerGetEvent getEvent) {
    if (prev != null)
      diffRelRckPos = getEvent.getGcpRelRckPos() - prev.getGcpRelRckPos();
    prev = getEvent;
  }

  @Override
  public Optional<SteerPutEvent> putEvent() {
    Tensor velocity = Objects.nonNull(lidarLocalizationModule) //
        ? lidarLocalizationModule.getVelocity() //
        : GokartPoseEvents.motionlessUninitialized().getVelocity();
    return steerColumnTracker.isCalibratedAndHealthy() //
        ? Optional.of(putEvent(steerColumnTracker.getSteerColumnEncoderCentered(), velocity, diffRelRckPos)) //
        : Optional.empty();
  }

  /** @param currangle with unit "SCE"
   * @param velocity {vx[m*s^-1], vy[m*s^-1], omega[s^-1]}
   * @param diffRelRckPos
   * @return */
  /* package */ SteerPutEvent putEvent(Scalar currangle, Tensor velocity, double diffRelRckPos) {
    // term1 is the static compensation of the restoring force, depending on the current angle
    // term2 is the compensation depending on the velocity of the steering wheel
    // term3 compensates the force caused by the lateral velocity in each front wheel
    Tensor filteredVel = geodesicIIR1Filter.apply(velocity);
    Scalar term1 = currangle.multiply(hapticSteerConfig.staticCompensation);
    // System.out.println(term1);
    Scalar term2 = hapticSteerConfig.dynamicCompensationBoundaryClip().apply( //
        RealScalar.of(diffRelRckPos).multiply(hapticSteerConfig.dynamicCompensation));
    // System.out.println(term2);
    AxleConfiguration axleConfiguration = RimoAxleConfiguration.frontFromSCE(currangle);
    Scalar latFront_LeftVel = axleConfiguration.wheel(0).adjoint(filteredVel).Get(1);
    Scalar latFrontRightVel = axleConfiguration.wheel(1).adjoint(filteredVel).Get(1);
    Scalar term3 = hapticSteerConfig.latForceCompensationBoundaryClip().apply( //
        latFront_LeftVel.add(latFrontRightVel).multiply(hapticSteerConfig.latForceCompensation));
    // System.out.println(term3);
    Scalar term4 = prev.tsuTrq().multiply(hapticSteerConfig.tsuFactor);
    return SteerPutEvent.createOn(term1.add(term2).add(term3).add(term4));
  }
}
