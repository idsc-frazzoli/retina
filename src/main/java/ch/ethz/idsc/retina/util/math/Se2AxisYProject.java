// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.qty.Units;
import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.Ramp;
import ch.ethz.idsc.tensor.sca.SignInterface;

class MapSingular implements TensorScalarFunction {
  private static final Scalar[] SIGNUM = //
      { DoubleScalar.NEGATIVE_INFINITY, RealScalar.ZERO, DoubleScalar.POSITIVE_INFINITY };
  // ---
  final Unit unit;

  MapSingular(Unit unit) {
    this.unit = unit;
  }

  @Override
  public Scalar apply(Tensor p) {
    Scalar px = p.Get(0);
    SignInterface signInterface = (SignInterface) px;
    return Quantity.of(SIGNUM[1 + signInterface.signInt()], unit);
  }
}

class MapPositive implements TensorScalarFunction {
  private final Scalar vx;
  private final Scalar be;

  MapPositive(Scalar vx, Scalar be) {
    this.vx = vx;
    this.be = be;
  }

  @Override
  public Scalar apply(Tensor p) {
    Scalar px = p.Get(0);
    Scalar py = p.Get(1);
    return ArcTan.of(vx.subtract(py.multiply(be)), px.multiply(be)).divide(be);
  }
}

class MapNegative implements TensorScalarFunction {
  private final Scalar vx;
  private final Scalar be;

  MapNegative(Scalar vx, Scalar be) {
    this.vx = vx;
    this.be = be;
  }

  @Override
  public Scalar apply(Tensor p) {
    Scalar px = p.Get(0).negate();
    Scalar py = p.Get(1);
    return ArcTan.of(vx.subtract(py.multiply(be)), px.multiply(be)).divide(be);
  }
}

// TODO not quite entirely unlike the last implementation
public enum Se2AxisYProject {
  ;
  /** @param u == {vx, 0, rate} with units {[m*s^-1], ?, [rad*s^-1]}
   * @param p == {px, py} with units {[m], [m]}
   * @return time to arrival of a point on the y axis that is subject to flow x to reach p.
   * negative return values are also possible. */
  public static TensorScalarFunction of(Tensor u) {
    Scalar vx = u.Get(0);
    Scalar be = u.Get(2);
    if (Scalars.isZero(be)) { // prevent division by 0 after arc tan
      if (Scalars.isZero(vx)) // prevent division by 0 of px
        return new MapSingular(Units.of(be).negate());
      return p -> p.Get(0).divide(vx);
    }
    return Ramp.of(vx).equals(vx) //
        ? new MapPositive(vx, be)
        : new MapNegative(vx.negate(), be.negate());
  }
}
