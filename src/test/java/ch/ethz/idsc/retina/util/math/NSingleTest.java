// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.DecimalScalar;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.StringScalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;
import junit.framework.TestCase;

public class NSingleTest extends TestCase {
  public void testQuantity() {
    try {
      NSingle.INSTANCE.apply(Quantity.of(3, "s"));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFraction() {
    Scalar scalar = NSingle.INSTANCE.apply(RationalScalar.of(1, 2));
    assertEquals(scalar.toString(), "0.5");
  }

  public void testInteger() {
    Scalar scalar = NSingle.INSTANCE.apply(RealScalar.of(-2345891274545L));
    assertEquals(scalar.toString(), "-2345891274545");
  }

  public void testString1() {
    Scalar s = StringScalar.of("here!");
    assertEquals(NSingle.INSTANCE.apply(s).toString(), "\"here!\"");
  }

  public void testString2() {
    String string = "\"here!\"";
    Scalar s = StringScalar.of(string);
    assertEquals(NSingle.INSTANCE.apply(s).toString(), string);
  }

  public void testDecimal() {
    Scalar scalar = (Scalar) DoubleScalar.of(0.25).map(Round._6);
    assertTrue(scalar instanceof DecimalScalar);
    scalar = NSingle.INSTANCE.apply(scalar);
    assertTrue(scalar instanceof DecimalScalar);
  }
}
