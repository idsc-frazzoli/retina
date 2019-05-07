// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.red.Times;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sca.Sqrt;

public class InverseCubic implements ScalarUnaryOperator {
  private static final Scalar _4 = RealScalar.of(4);
  private static final Scalar _9 = RealScalar.of(9);
  private static final Scalar _27 = RealScalar.of(27);
  private static final Scalar _R3 = Sqrt.of(RealScalar.of(3));
  // ---
  private final Scalar a;
  private final Scalar b;
  private final Scalar a3;
  private final Scalar a4;
  private final Scalar b3;

  public InverseCubic(Scalar a, Scalar b) {
    this.a = a;
    this.b = b;
    a3 = Times.of(a, a, a);
    b3 = Times.of(b, b, b);
    a4 = a3.multiply(a);
  }

  @Override
  public Scalar apply(Scalar y) {
    Scalar n1 = CubeRoot.FUNCTION.apply(Times.of(_9, a, a, y).add(_R3.multiply(Sqrt.of(Times.of(_4, a3, b3).add(Times.of(_27, a4, y, y))))));
    Scalar f1 = n1.divide(Times.of(CubeRoot.FUNCTION.apply(RealScalar.of(2)), Power.of(3, RationalScalar.of(2, 3)), a));
    Scalar f2 = Times.of(CubeRoot.FUNCTION.apply(RationalScalar.of(2, 3)), b).divide(n1);
    return f1.subtract(f2);
  }
}
