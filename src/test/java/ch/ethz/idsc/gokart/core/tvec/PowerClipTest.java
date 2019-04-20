// code by jph
package ch.ethz.idsc.gokart.core.tvec;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class PowerClipTest extends TestCase {
  public void testSimple() {
    PowerClip powerClip = new PowerClip( //
        Quantity.of(10, SI.ACCELERATION), //
        Quantity.of(12, SI.ACCELERATION));
    assertEquals(powerClip.relative(Quantity.of(10.5, SI.ACCELERATION)), RationalScalar.HALF.negate());
    assertEquals(powerClip.relative(Quantity.of(13.5, SI.ACCELERATION)), RealScalar.ONE);
    assertEquals(powerClip.absolute(RationalScalar.HALF.negate()), Quantity.of(10.5, SI.ACCELERATION));
    assertEquals(powerClip.absolute(RationalScalar.HALF), Quantity.of(11.5, SI.ACCELERATION));
    assertEquals(powerClip.absolute(RealScalar.ONE), Quantity.of(12, SI.ACCELERATION));
  }

  public void testReversed() {
    PowerClip powerClip = new PowerClip( //
        Quantity.of(12, SI.ACCELERATION), //
        Quantity.of(10, SI.ACCELERATION));
    assertEquals(powerClip.relative(Quantity.of(10.5, SI.ACCELERATION)), RationalScalar.HALF);
    assertEquals(powerClip.relative(Quantity.of(13.5, SI.ACCELERATION)), RealScalar.ONE.negate());
    assertEquals(powerClip.absolute(RationalScalar.HALF.negate()), Quantity.of(11.5, SI.ACCELERATION));
    assertEquals(powerClip.absolute(RationalScalar.HALF), Quantity.of(10.5, SI.ACCELERATION));
    assertEquals(powerClip.absolute(RealScalar.ONE), Quantity.of(10, SI.ACCELERATION));
  }
}
