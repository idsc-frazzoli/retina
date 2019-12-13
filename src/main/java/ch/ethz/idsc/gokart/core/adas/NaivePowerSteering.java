// code by ta, em, jph
package ch.ethz.idsc.gokart.core.adas;

import ch.ethz.idsc.gokart.calib.steer.RimoAxleConfiguration;
import ch.ethz.idsc.owl.car.core.AxleConfiguration;
import ch.ethz.idsc.sophus.flt.ga.GeodesicIIR1;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class NaivePowerSteering extends PowerSteering {
  private final GeodesicIIR1 velocityGeodesicIIR1;

  public NaivePowerSteering(HapticSteerConfig hapticSteerConfig) {
    super(hapticSteerConfig);
    velocityGeodesicIIR1 = new GeodesicIIR1(RnGeodesic.INSTANCE, hapticSteerConfig.velocityFilter);
  }

  @Override
  Scalar term1(Scalar currangle, Tensor velocity) {
    AxleConfiguration axleConfiguration = RimoAxleConfiguration.frontFromSCE(currangle);
    Tensor filteredVel = velocityGeodesicIIR1.apply(velocity);
    Scalar latFront_LeftVel = axleConfiguration.wheel(0).adjoint(filteredVel).Get(1);
    Scalar latFrontRightVel = axleConfiguration.wheel(1).adjoint(filteredVel).Get(1);
    return hapticSteerConfig.latForceCompensationBoundaryClip().apply( //
        latFront_LeftVel.add(latFrontRightVel).multiply(hapticSteerConfig.latForceCompensation));
  }
}
