// code by am
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** class is used to develop and test anti lock brake logic */
public class LaneKeepingSlowDownModule extends LaneKeepingCenterlineModule implements RimoPutProvider {
  private final MeasurementSlowDownModule slowDown = new MeasurementSlowDownModule();
  private final LeftLaneModule leftLaneModule = new LeftLaneModule();
  private Scalar slowDownDistance = Quantity.of(1, SI.METER);

  @Override
  public Optional<RimoPutEvent> putEvent() {
    // TODO AM this logic does not need to happen for every rimo put event
    // ... instead 10[Hz] would be sufficient, or for every pose update
    if (leftLaneModule.leftLane(optionalCurve, gokartPoseEvent, slowDownDistance)) {
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
