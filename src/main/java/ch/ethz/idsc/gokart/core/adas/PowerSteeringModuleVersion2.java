// code by am and jph
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.RimoAxleConfiguration;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerGetListener;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutProvider;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.owl.car.core.AxleConfiguration;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.sophus.filter.GeodesicIIR1Filter;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Min;

public class PowerSteeringModuleVersion2 extends AbstractModule implements SteerGetListener, SteerPutProvider {
  private final SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final LidarLocalizationModule lidarLocalizationModule = ModuleAuto.INSTANCE.getInstance(LidarLocalizationModule.class);
  private final GeodesicIIR1Filter geodesicIIR1Filter = new GeodesicIIR1Filter( //
      RnGeodesic.INSTANCE, HapticSteerConfig.GLOBAL.velocityFilter); // 1 means unfiltered

  @Override
  protected void first() {
    SteerSocket.INSTANCE.addGetListener(this);
    SteerSocket.INSTANCE.addPutProvider(this);
  }

  @Override
  protected void last() {
    SteerSocket.INSTANCE.removeGetListener(this);
    SteerSocket.INSTANCE.removePutProvider(this);
  }

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
  public ProviderRank getProviderRank() {
    return ProviderRank.MANUAL;
  }

  @Override
  public Optional<SteerPutEvent> putEvent() {
    if (steerColumnTracker.isCalibratedAndHealthy()) {
      return Optional.of(putEvent(steerColumnTracker.getSteerColumnEncoderCentered()));
    }
    return Optional.empty();
  }

  private Scalar latFrontLeftVel;
  private Scalar latFrontRightVel;

  public void getFrontVelocity() {
    if (steerColumnTracker.isCalibratedAndHealthy()) {
      if (lidarLocalizationModule != null) {
        AxleConfiguration axleConfiguration = RimoAxleConfiguration.frontFromSCE(steerColumnTracker.getSteerColumnEncoderCentered());
        {
          Tensor velocity = lidarLocalizationModule.getVelocity();
          Tensor filteredVel = geodesicIIR1Filter.apply(velocity);
          latFrontLeftVel = axleConfiguration.wheel(0).adjoint(filteredVel).Get(1);
          latFrontRightVel = axleConfiguration.wheel(1).adjoint(filteredVel).Get(1);
        }
      }
    }
  }

  public SteerPutEvent putEvent(Scalar currangle) {
    // term1 is the static compensation of the restoring force, depending on the current angle
    // term2 is the compensation depending on the velocity of the steering wheel
    // term3 compensates the force caused by the lateral velocity in each front wheel
    Scalar term1 = currangle.multiply(HapticSteerConfig.GLOBAL.staticCompensation);
    Scalar term2 = Min.of( //
        RealScalar.of(diffRelRckPos).multiply(HapticSteerConfig.GLOBAL.dynamicCompensation), //
        HapticSteerConfig.GLOBAL.dynamicCompensationBoundary);
    Scalar term3 = Min.of(//
        (latFrontLeftVel.add(latFrontRightVel)).multiply(HapticSteerConfig.GLOBAL.latForceCompensation), //
        HapticSteerConfig.GLOBAL.latForceCompensationBoundary);
    return SteerPutEvent.createOn(term1.add(term2).add(term3));
  }
  // return Optional.of(SteerPutEvent.createOn(Quantity.of(diffRelRckPos > 0 ? 0.3 : -0.3, "SCT")));
  // }
}
