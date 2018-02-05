// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.StringScalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class NSingleTest extends TestCase {
  public void testQuantity() {
    Scalar scalar = NSingle.FUNCTION.apply(Quantity.of(3, "s"));
    assertEquals(scalar.toString(), "3[s]");
  }

  public void testFraction() {
    Scalar scalar = NSingle.FUNCTION.apply(RationalScalar.of(1, 2));
    assertEquals(scalar.toString(), "0.5");
  }

  public void testInteger() {
    Scalar scalar = NSingle.FUNCTION.apply(RealScalar.of(-2345891274545L));
    assertEquals(scalar.toString(), "-2345891274545");
  }

  public void testString1() {
    Scalar s = StringScalar.of("here!");
    assertEquals(NSingle.FUNCTION.apply(s).toString(), "\"here!\"");
  }

  public void testString2() {
    String string = "\"here!\"";
    Scalar s = StringScalar.of(string);
    assertEquals(NSingle.FUNCTION.apply(s).toString(), string);
  }
}
