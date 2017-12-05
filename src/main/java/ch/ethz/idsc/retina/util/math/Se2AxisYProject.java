// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Units;
import ch.ethz.idsc.tensor.sca.ArcTan;

public enum Se2AxisYProject {
  ;
  /** @param x == {vx, 0, rate} with units {[m*s^-1], ?, [rad*s^-1]}
   * @param p == {px, py} with units {[m], [m]}
   * @return time to arrival of a point on the y axis that is subject to flow x to reach p */
  public static Scalar of(Tensor x, Tensor p) {
    Scalar vx = x.Get(0);
    Scalar be = x.Get(2);
    Scalar px = p.Get(0);
    Scalar py = p.Get(1);
    if (Scalars.isZero(be)) { // prevent division by 0 after arc tan
      if (Scalars.isZero(vx)) // prevent division by 0 of px
        return Quantity.of(Scalars.isZero(px) // map to extremes
            ? RealScalar.ZERO
            : DoubleScalar.POSITIVE_INFINITY, Units.of(be).negate());
      return px.divide(vx);
    }
    return ArcTan.of(vx.subtract(py.multiply(be)), px.multiply(be)).divide(be);
  }
}
