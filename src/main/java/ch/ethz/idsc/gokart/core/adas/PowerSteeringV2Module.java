// code by am and jph
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.RimoAxleConfiguration;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.owl.car.core.AxleConfiguration;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.sophus.filter.GeodesicIIR1Filter;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Min;

public class PowerSteeringV2Module extends PowerSteeringBaseModule {
  private final SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final LidarLocalizationModule lidarLocalizationModule = ModuleAuto.INSTANCE.getInstance(LidarLocalizationModule.class);
  private final GeodesicIIR1Filter geodesicIIR1Filter = new GeodesicIIR1Filter( //
      RnGeodesic.INSTANCE, HapticSteerConfig.GLOBAL.velocityFilter); // 1 means unfiltered
  // ---
  private SteerGetEvent prev;
  private double diffRelRckPos;

  @Override
  public void getEvent(SteerGetEvent getEvent) {
    if (prev != null) {
      diffRelRckPos = getEvent.getGcpRelRckPos() - prev.getGcpRelRckPos();
    }
    prev = getEvent;
  }

  @Override
  public Optional<SteerPutEvent> putEvent() {
    return steerColumnTracker.isCalibratedAndHealthy() //
        ? Optional.of(putEvent(steerColumnTracker.getSteerColumnEncoderCentered()))
        : Optional.empty();
  }

  public SteerPutEvent putEvent(Scalar currangle) {
    // term1 is the static compensation of the restoring force, depending on the current angle
    // term2 is the compensation depending on the velocity of the steering wheel
    // term3 compensates the force caused by the lateral velocity in each front wheel
    Scalar latFrontLeftVel = Quantity.of(0, SI.VELOCITY);
    Scalar latFrontRightVel = Quantity.of(0, SI.VELOCITY);
    if (lidarLocalizationModule != null) {
      AxleConfiguration axleConfiguration = RimoAxleConfiguration.frontFromSCE(currangle);
      {
        Tensor velocity = lidarLocalizationModule.getVelocity();
        Tensor filteredVel = geodesicIIR1Filter.apply(velocity);
        latFrontLeftVel = axleConfiguration.wheel(0).adjoint(filteredVel).Get(1);
        latFrontRightVel = axleConfiguration.wheel(1).adjoint(filteredVel).Get(1);
      }
    }
    Scalar term1 = currangle.multiply(HapticSteerConfig.GLOBAL.staticCompensation);
    // TODO need to clip [-boundary, boundary] with dynamicCompensationBoundary
    Scalar term2 = Min.of( //
        RealScalar.of(diffRelRckPos).multiply(HapticSteerConfig.GLOBAL.dynamicCompensation), //
        HapticSteerConfig.GLOBAL.dynamicCompensationBoundary);
    // TODO need to clip [-boundary, boundary] with latForceCompensationBoundary
    Scalar term3 = Min.of( //
        (latFrontLeftVel.add(latFrontRightVel)).multiply(HapticSteerConfig.GLOBAL.latForceCompensation), //
        HapticSteerConfig.GLOBAL.latForceCompensationBoundary);
    return SteerPutEvent.createOn(term1.add(term2).add(term3));
  }
}
