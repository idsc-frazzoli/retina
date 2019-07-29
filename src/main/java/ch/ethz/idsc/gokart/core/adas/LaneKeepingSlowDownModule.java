// code by am
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;

/** class is used apply the slowDownMeasurement when the lane is left */
/* package */ class LaneKeepingSlowDownModule extends LaneKeepingCenterlineModule implements RimoPutProvider {
  private final MeasurementSlowDownModule slowDown = new MeasurementSlowDownModule();
  private Scalar slowDownDistance = Quantity.of(1, SI.METER);

  @Override // from AbstractClockedModule
  protected synchronized void runAlgo() {
    boolean isQualityOk = LocalizationConfig.GLOBAL.isQualityOk(gokartPoseEvent);
    Tensor pose = isQualityOk //
        ? gokartPoseEvent.getPose() //
        : GokartPoseEvents.motionlessUninitialized().getPose();
    velocity = isQualityOk //
        ? gokartPoseEvent.getVelocity() //
        : GokartPoseEvents.motionlessUninitialized().getVelocity();
    boolean isPresent = optionalCurve.isPresent();
    Tensor curve = isPresent //
        ? optionalCurve.get()//
        : null;
    if (isPresent && isQualityOk) {
      optionalPermittedRange = getPermittedRange(curve, pose);
      System.out.println(optionalPermittedRange);
    }
    if (isPresent && isQualityOk) {
      this.putEvent();
    }
  }

  @Override
  public Optional<RimoPutEvent> putEvent() {
    if (LaneHelper.leftLane(optionalCurve, gokartPoseEvent, slowDownDistance)) {
      System.out.println("left lane");
      return slowDown.putEvent();
    }
    System.out.println("still on lane");
    return Optional.empty();
  }

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }
}
