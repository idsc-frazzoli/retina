// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Times;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class CubeRootTest extends TestCase {
  public void testSimple() {
    Scalar scalar = CubeRoot.FUNCTION.apply(RealScalar.of(27));
    Chop._12.requireClose(scalar, RealScalar.of(3));
  }

  public void testQuantity() {
    Scalar input = Quantity.of(2, SI.METER.multiply(RealScalar.of(3)));
    Scalar scalar = CubeRoot.FUNCTION.apply(input);
    Chop._12.requireClose(scalar, Quantity.of(1.2599210498948732, SI.METER));
    Chop._12.requireClose(Times.of(scalar, scalar, scalar), input);
  }

  public void testNegative() {
    Scalar input = Quantity.of(-2, SI.METER.multiply(RealScalar.of(3)));
    Scalar scalar = CubeRoot.FUNCTION.apply(input);
    Chop._12.requireClose(scalar, Quantity.of(-1.2599210498948731648, SI.METER));
  }

  public void testComplexFail() {
    Scalar scalar = ComplexScalar.of(12, 23);
    try {
      CubeRoot.FUNCTION.apply(scalar);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
