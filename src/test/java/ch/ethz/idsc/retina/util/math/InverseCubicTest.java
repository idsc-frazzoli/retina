// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class InverseCubicTest extends TestCase {
  public void testSimple() {
    Scalar a = RealScalar.of(2);
    Scalar b = RealScalar.of(5);
    ScalarUnaryOperator cubic = Series.of(Tensors.of(RealScalar.ZERO, b, RealScalar.ZERO, a));
    Scalar x = RealScalar.of(-3);
    Scalar value = cubic.apply(x);
    InverseCubic inverseCubic = new InverseCubic(a, b);
    Scalar res = inverseCubic.apply(value);
    Chop._10.requireClose(res, x);
  }

  public void testSteer() {
    Scalar a = RealScalar.of(-0.33633373640449604);
    Scalar b = RealScalar.of(+0.8284521034333863);
    ScalarUnaryOperator cubic = Series.of(Tensors.of(RealScalar.ZERO, b, RealScalar.ZERO, a));
    Scalar x = RealScalar.of(0.1);
    Scalar value = cubic.apply(x);
    InverseCubic inverseCubic = new InverseCubic(a, b);
    Scalar res = inverseCubic.apply(value);
    System.out.println(res);
  }
}
