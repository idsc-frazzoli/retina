// code by edo
// code adapted by jph
package ch.ethz.idsc.owl.car.model;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public enum MotorTorques {
  ;
  private static final Scalar HALF = RealScalar.of(.5);

  /** @param gammaM rear/total drive ratio; 0 is FWD, 1 is RWD, 0.5 is 4WD
   * @param throttle absolute [Nm] */
  public static Tensor standard(Scalar gammaM, Scalar throttle) {
    Scalar reqTorque = throttle.multiply(HALF);
    Scalar rearCoeff = gammaM;
    Scalar frontCoeff = RealScalar.ONE.subtract(rearCoeff);
    Scalar Tm1L = frontCoeff.multiply(reqTorque);
    Scalar Tm1R = Tm1L;
    Scalar Tm2L = rearCoeff.multiply(reqTorque);
    Scalar Tm2R = Tm2L;
    return Tensors.of(Tm1L, Tm1R, Tm2L, Tm2R);
  }

  /** @param throttleL
   * @param throttleR
   * @return throttle on left/right rear tires according to input,
   * zero throttle on the front wheels */
  public static Tensor electonicGokart(Scalar throttleL, Scalar throttleR) {
    return Tensors.of( //
        RealScalar.ZERO, RealScalar.ZERO, //
        throttleL, throttleR);
  }
}
