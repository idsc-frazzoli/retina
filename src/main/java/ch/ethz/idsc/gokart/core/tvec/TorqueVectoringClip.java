// code by mh, jph
package ch.ethz.idsc.gokart.core.tvec;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Ramp;

public enum TorqueVectoringClip {
  ;
  /** @param power unitless
   * @param wantedZTorque unitless
   * @return */
  public static Tensor from(Scalar power, Scalar wantedZTorque) {
    return of( //
        power.subtract(wantedZTorque), //
        power.add(wantedZTorque));
  }

  /** power left (powerL) and power right (powerR) should total to a value in the interval [-1, 1]
   * 
   * @param powerL power left unitless
   * @param powerR power right unitless
   * @return vector of length 2 with scalars in interval [-1, 1] */
  /* package */ static Tensor of(Scalar powerL, Scalar powerR) {
    Scalar l_hi = Ramp.FUNCTION.apply(powerL.subtract(RealScalar.ONE));
    Scalar l_lo = Ramp.FUNCTION.apply(powerL.negate().subtract(RealScalar.ONE));
    Scalar r_hi = Ramp.FUNCTION.apply(powerR.subtract(RealScalar.ONE));
    Scalar r_lo = Ramp.FUNCTION.apply(powerR.negate().subtract(RealScalar.ONE));
    Scalar d1 = Max.of(l_hi, r_lo);
    Scalar d2 = Max.of(r_hi, l_lo);
    Scalar delta = d2.subtract(d1);
    return Tensors.of( //
        powerL.add(delta), //
        powerR.subtract(delta)).map(Clips.absoluteOne());
  }
}
