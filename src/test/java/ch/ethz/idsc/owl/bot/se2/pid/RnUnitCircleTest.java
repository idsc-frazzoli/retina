// code by mcp
package ch.ethz.idsc.owl.bot.se2.pid;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class RnUnitCircleTest extends TestCase {
  public void testConvertPos() {
    Scalar angle = Pi.TWO.multiply(RealScalar.of(5)).add(Pi.HALF);
    angle = RnUnitCircle.convert(angle);
    System.out.println(angle);
    System.out.println(Pi.HALF);
    Chop._10.requireClose(Pi.HALF, angle);
  }

  public void testConvertNeg() {
    Scalar angle = Pi.TWO.multiply(RealScalar.of(5)).add(Pi.HALF).negate();
    angle = RnUnitCircle.convert(angle);
    System.out.println(angle);
    System.out.println(Pi.HALF.negate());
    Chop._12.requireClose(Pi.HALF.negate(), angle);
  }
}
