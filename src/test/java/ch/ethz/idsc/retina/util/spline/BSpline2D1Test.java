// code by jph
package ch.ethz.idsc.retina.util.spline;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class BSpline2D1Test extends TestCase {
  public void testBPD1Outside() {
    assertEquals(BSpline2D1.FUNCTION.apply(RealScalar.of(-1)), RealScalar.ZERO);
    assertEquals(BSpline2D1.FUNCTION.apply(RealScalar.of(+0)), RealScalar.ZERO);
    assertEquals(BSpline2D1.FUNCTION.apply(RealScalar.of(+3)), RealScalar.ZERO);
    assertEquals(BSpline2D1.FUNCTION.apply(RealScalar.of(+4)), RealScalar.ZERO);
  }

  public void testBPD1s1() {
    Scalar scalar = BSpline2D1.FUNCTION.apply(RationalScalar.of(1, 3));
    assertEquals(scalar, RationalScalar.of(1, 3));
  }

  public void testBPD1s2() {
    Scalar scalar = BSpline2D1.FUNCTION.apply(RationalScalar.of(4, 3));
    assertTrue(Chop._12.close(scalar, RationalScalar.of(1, 3)));
  }

  public void testBPD1s3() {
    Scalar scalar = BSpline2D1.FUNCTION.apply(RationalScalar.of(7, 3));
    assertTrue(Chop._12.close(scalar, RationalScalar.of(-2, 3)));
  }
}
