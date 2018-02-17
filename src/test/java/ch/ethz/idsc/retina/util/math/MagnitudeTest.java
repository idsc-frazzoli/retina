// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.qty.UnitConvert;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class MagnitudeTest extends TestCase {
  public void testSimple() {
    Scalar duration = Quantity.of(1, "ms");
    ScalarUnaryOperator suo = UnitConvert.SI().to(Unit.of("s")).andThen(QuantityMagnitude.singleton("s"))::apply;
    assertEquals(suo.apply(duration), RationalScalar.of(1, 1000));
  }

  public void testSimple2() {
    Scalar scalar = Quantity.of(2500, "mV");
    Scalar result = Magnitude.VOLT.apply(scalar);
    assertEquals(result, RationalScalar.of(5, 2));
  }

  public void testUnitOne() {
    Scalar scalar = Quantity.of(2500, "rad*deg");
    Scalar result = Magnitude.ONE.apply(scalar);
    assertTrue(result instanceof RealScalar);
  }

  public void testKnots() {
    Scalar scalar = Quantity.of(100, "knots");
    Scalar fraction = Magnitude.VELOCITY.apply(scalar);
    assertEquals(fraction, RationalScalar.of(463, 9));
    assertTrue(ExactScalarQ.of(fraction));
  }
}
