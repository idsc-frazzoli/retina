// code by edo and jph
package ch.ethz.idsc.owl.car.math;

import java.io.Serializable;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Hypot;

/** slip as introduced in textbook,
 * straight forward implementation suffers from numerical badness.
 * 
 * Important: use {@link RobustSlip} instead */
class TextbookSlip implements SlipInterface, Serializable {
  private static final Scalar EPS = RealScalar.of(1e-8);
  // ---
  private final Scalar mux;
  private final Scalar muy;

  /** if U == (rtw, 0) that means no slip
   * 
   * @param pacejka3
   * @param U ground speed in coordinate system of tire
   * @param rtw == radius * rate of wheel */
  public TextbookSlip(Pacejka3 pacejka3, Tensor U, Scalar rtw) {
    final Scalar vx = U.Get(0);
    final Scalar vy = U.Get(1);
    final Scalar sx = vx.subtract(rtw).divide(rtw); // division by 0 !
    final Scalar sy = RealScalar.ONE.add(sx).multiply(vy.divide(vx));
    final Scalar s = Hypot.of(sx, sy);
    final Scalar mu = pacejka3.apply(s);
    mux = mu.multiply(robustDiv(sx, s, EPS)).negate(); // hack !
    muy = mu.multiply(robustDiv(sy, s, EPS)).negate(); // hack !
  }

  @Override // from SlipInterface
  public Tensor slip() {
    return Tensors.of(mux, muy);
  }

  private static Scalar robustDiv(Scalar num, Scalar den, Scalar eps) {
    if (Scalars.isZero(den)) {
      if (Scalars.nonZero(num))
        return num.divide(eps);
      return RealScalar.ZERO;
    }
    return num.divide(den);
  }
}
