// code by am
package ch.ethz.idsc.gokart.core.adas;

import ch.ethz.idsc.gokart.calib.steer.RimoAxleConfiguration;
import ch.ethz.idsc.gokart.calib.steer.SteerFeedForwardConfig;
import ch.ethz.idsc.owl.car.core.AxleConfiguration;
import ch.ethz.idsc.sophus.flt.ga.GeodesicIIR1;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class PowerSteering {
  private final HapticSteerConfig hapticSteerConfig;
  private final GeodesicIIR1 geodesicIIR1; // 1 means unfiltered

  public PowerSteering(HapticSteerConfig hapticSteerConfig) {
    this.hapticSteerConfig = hapticSteerConfig;
    geodesicIIR1 = new GeodesicIIR1(RnGeodesic.INSTANCE, hapticSteerConfig.velocityFilter);
  }

  /** @param currangle with unit "SCE"
   * @param velocity {vx[m*s^-1], vy[m*s^-1], omega[s^-1]}
   * @param tsu torque exerted by driver
   * @return scalar with unit SCT */
  public Scalar torque(Scalar currangle, Tensor velocity, Scalar tsu) {
    // term0 is the static compensation of the restoring force, depending on the current angle
    // term1 is the compensation depending on the velocity of the steering wheel
    // term2 amplifies the torque exerted by the driver
    Scalar feedForwardValue = SteerFeedForwardConfig.GLOBAL.series().apply(currangle);
    Scalar term0 = hapticSteerConfig.feedForward //
        ? feedForwardValue
        : feedForwardValue.zero();
    // ---
    AxleConfiguration axleConfiguration = RimoAxleConfiguration.frontFromSCE(currangle);
    Tensor filteredVel = geodesicIIR1.apply(velocity);
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
}
