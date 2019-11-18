// code by ta, em, jph
package ch.ethz.idsc.gokart.core.adas;

import ch.ethz.idsc.gokart.calib.steer.TricycleFrontAxleConfiguration;
import ch.ethz.idsc.owl.car.core.AxleConfiguration;
import ch.ethz.idsc.owl.car.core.WheelConfiguration;
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
    AxleConfiguration axleConfiguration = new TricycleFrontAxleConfiguration(currangle);
    Tensor filteredVel = velocityGeodesicIIR1.apply(velocity);
    WheelConfiguration wheelConfiguration = axleConfiguration.wheel(0);
    Tensor V = wheelConfiguration.adjoint(filteredVel);
    Scalar Vx = V.Get(0);
    Scalar Vy = V.Get(1);
    Scalar alpha = Vy.divide(Vx.add(Quantity.of(0.5, "m*s^-1")));
    Scalar dist = hapticSteerConfig.getPacejkaDistance().apply(alpha);
    Scalar force = hapticSteerConfig.getPacejkaForce().apply(alpha);
    return hapticSteerConfig.latForceCompensationBoundaryClip().apply(dist.multiply(force));
  }
}
