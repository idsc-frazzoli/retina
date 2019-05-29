// code by mh
package ch.ethz.idsc.gokart.calib.power;

import ch.ethz.idsc.gokart.core.tvec.TorqueVectoringClip;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum StaticHelper {
  ;
  private static final PowerLookupTable POWER_LOOKUP_TABLE = PowerLookupTable.getInstance();

  /** @param wantedAcceleration [m*s^-2]
   * @param wantedZTorque [ONE]
   * @param velocity [m/s]
   * @return vector of length 2 with required motor currents [ARMS] */
  // TODO JPH/MH write tests specifically for method getAdvancedMotorCurrents
  /* package */ static Tensor getAdvancedMotorCurrents(Scalar wantedAcceleration, Scalar wantedZTorque, Scalar velocity) {
    Tensor minMax = POWER_LOOKUP_TABLE.getMinMaxAcceleration(velocity);
    // get clipped individual accelerations
    PowerClip powerClip = new PowerClip(minMax.Get(0), minMax.Get(1));
    Tensor wantedAccelerations = //
        TorqueVectoringClip.from(powerClip.relative(wantedAcceleration), wantedZTorque) //
            .map(powerClip::absolute);
    return Tensors.of( //
        POWER_LOOKUP_TABLE.getNeededCurrent(wantedAccelerations.Get(0), velocity), //
        POWER_LOOKUP_TABLE.getNeededCurrent(wantedAccelerations.Get(1), velocity));
  }
}
