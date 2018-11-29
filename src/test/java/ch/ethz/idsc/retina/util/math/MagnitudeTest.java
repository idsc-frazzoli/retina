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
    assertEquals(Magnitude.VOLT.toDouble(scalar), 2.5);
    assertEquals(Magnitude.VOLT.toInt(scalar), 2);
    assertEquals(Magnitude.VOLT.toLong(scalar), 2);
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

  public void testRadPerSec() {
    Scalar s1 = Quantity.of(100, SI.PER_SECOND);
    Scalar s2 = Quantity.of(100, SIDerived.RADIAN_PER_SECOND);
    assertEquals(Magnitude.PER_SECOND.apply(s1), Magnitude.PER_SECOND.apply(s2));
    assertEquals(Magnitude.PER_SECOND.toDouble(s1), 100.0);
    assertEquals(Magnitude.PER_SECOND.toInt(s1), 100);
    assertEquals(Magnitude.PER_SECOND.toLong(s1), 100);
  }

  public void testFail() {
    Scalar scalar = Quantity.of(100, "s*knots");
    try {
      Magnitude.VELOCITY.apply(scalar);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailOne() {
    Scalar scalar = Quantity.of(100, "s*knots");
    try {
      Magnitude.ONE.apply(scalar);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailToDouble() {
    Scalar scalar = Quantity.of(100, "s*knots");
    try {
      Magnitude.MICRO_SECOND.toDouble(scalar);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
