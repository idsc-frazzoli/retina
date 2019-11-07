// code by am
package ch.ethz.idsc.gokart.core.adas;

import ch.ethz.idsc.gokart.calib.steer.SteerFeedForward;
import ch.ethz.idsc.sophus.flt.ga.GeodesicIIR1;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** implementation of manual power steering consisting of three terms:
 * {@link SteerFeedForward}
 * lateral force compensation
 * tsuTrq, i.e. torque by driver
 * 
 * Reference:
 * "Advanced Driver Assistance Systems on a Go-Kart" by A. Mosberger */
abstract /* package */ class PowerSteering {
  protected final HapticSteerConfig hapticSteerConfig;
  private final GeodesicIIR1 tsuGeodesicIIR1; // 1 means unfiltered

  public PowerSteering(HapticSteerConfig hapticSteerConfig) {
    this.hapticSteerConfig = hapticSteerConfig;
    tsuGeodesicIIR1 = new GeodesicIIR1(RnGeodesic.INSTANCE, hapticSteerConfig.tsuFilter);
  }

  /** @param currangle with unit "SCE"
   * @param velocity {vx[m*s^-1], vy[m*s^-1], omega[s^-1]}
   * @param tsu torque exerted by driver
   * @return scalar with unit SCT */
  public Scalar torque(Scalar currangle, Tensor velocity, Scalar tsu) {
    // term0 is the static compensation of the restoring force, depending on the current angle
    // term1 is the compensation depending on the velocity of the steering wheel
    // term2 amplifies the torque exerted by the driver
    Scalar feedForwardValue = SteerFeedForward.FUNCTION.apply(currangle);
    Scalar term0 = hapticSteerConfig.feedForward //
        ? feedForwardValue
        : feedForwardValue.zero();
    // ---
    Scalar term1 = term1(currangle, velocity);
    // ---
    Scalar filteredTsu = tsuGeodesicIIR1.apply(tsu).Get();
    Scalar term2 = filteredTsu.multiply(hapticSteerConfig.tsuFactor);
    return term0.add(term1).add(term2);
  }

  /** @param currangle with unit "SCE"
   * @param velocity {vx[m*s^-1], vy[m*s^-1], omega[s^-1]}
   * @param tsu torque exerted by driver
   * @return scalar with unit SCT */
  abstract Scalar term1(Scalar currangle, Tensor velocity);
}
