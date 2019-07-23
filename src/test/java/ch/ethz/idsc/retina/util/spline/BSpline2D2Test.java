// code by jph
package ch.ethz.idsc.retina.util.spline;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class BSpline2D2Test extends TestCase {
  public void testBPD2Outside() {
    assertEquals(BSpline2D2.FUNCTION.apply(RealScalar.of(-1)), RealScalar.ZERO);
    assertEquals(BSpline2D2.FUNCTION.apply(RealScalar.of(+0)), RealScalar.ONE);
    assertEquals(BSpline2D2.FUNCTION.apply(RealScalar.of(+3)), RealScalar.ZERO);
    assertEquals(BSpline2D2.FUNCTION.apply(RealScalar.of(+4)), RealScalar.ZERO);
  }

  public void testBPD2s1() {
    Scalar scalar = BSpline2D2.FUNCTION.apply(RationalScalar.of(1, 3));
    assertEquals(scalar, RealScalar.ONE);
  }

  public void testBPD2s2() {
    Scalar scalar = BSpline2D2.FUNCTION.apply(RationalScalar.of(4, 3));
    Chop._12.requireClose(scalar, RealScalar.of(-2));
  }

  public void testBPD2s3() {
    Scalar scalar = BSpline2D2.FUNCTION.apply(RationalScalar.of(7, 3));
    Chop._12.requireClose(scalar, RealScalar.ONE);
  }
}
