// code by ta, em, jph
package ch.ethz.idsc.gokart.core.adas;

import ch.ethz.idsc.sophus.flt.ga.GeodesicIIR1;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ class PacejkaPowerSteering extends PowerSteering {
  private final GeodesicIIR1 velocityGeodesicIIR1;

  public PacejkaPowerSteering(HapticSteerConfig hapticSteerConfig) {
    super(hapticSteerConfig);
    velocityGeodesicIIR1 = new GeodesicIIR1(RnGeodesic.INSTANCE, hapticSteerConfig.velocityFilter);
  }

  @Override
  Scalar term1(Scalar currangle, Tensor velocity) {
    velocityGeodesicIIR1.apply(velocity);
    // TODO introduce ...
    return Quantity.of(0.0, "SCT");
  }
}
