// code by jph
package ch.ethz.idsc.gokart.calib.power;

import junit.framework.TestCase;

public class CubicBiPolynomialTest extends TestCase {
  public void testSimple() {
    CubicBiPolynomial cubicBiPolynomial = new CubicBiPolynomial( //
        0, //
        0, 0, //
        0, 0, 0, //
        1, 0, 0, 2);
    assertEquals(cubicBiPolynomial.evaluate(0, 2), 2 * 2 * 2 * 2f);
    assertEquals(cubicBiPolynomial.evaluate(2, 1), 2 * 2 * 2 + 2f);
  }
}
