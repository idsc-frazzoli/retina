// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class ClipzoneTest extends TestCase {
  public void testDeadzonePos() {
    Clipzone clipzone = new Clipzone(Clip.function(.1, 2));
    assertEquals(clipzone.apply(RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(clipzone.apply(RealScalar.of(.01)), RealScalar.ZERO);
    assertEquals(clipzone.apply(RealScalar.of(.1)), RealScalar.ZERO);
    assertTrue(Chop._14.close( //
        clipzone.apply(RealScalar.of(.2)), //
        RealScalar.of(0.10526315789473685)));
    assertEquals(clipzone.apply(RealScalar.of(2)), RealScalar.of(2));
    assertEquals(clipzone.apply(RealScalar.of(3)), RealScalar.of(2));
  }

  public void testDeadzoneNeg() {
    Clipzone clipzone = new Clipzone(Clip.function(.1, 2));
    assertEquals(clipzone.apply(RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(clipzone.apply(RealScalar.of(-.01)), RealScalar.ZERO);
    assertEquals(clipzone.apply(RealScalar.of(-.1)), RealScalar.ZERO);
    assertTrue(Chop._14.close( //
        clipzone.apply(RealScalar.of(-.2)), //
        RealScalar.of(-0.10526315789473685)));
    assertEquals(clipzone.apply(RealScalar.of(-2)), RealScalar.of(-2));
    assertEquals(clipzone.apply(RealScalar.of(-3)), RealScalar.of(-2));
  }

  public void testDeadzoneQuantity() {
    Clipzone clipzone = new Clipzone(Clip.function(Quantity.of(RationalScalar.HALF, "m"), Quantity.of(RationalScalar.of(5, 2), "m")));
    {
      Scalar result = clipzone.apply(Quantity.of(0.0, "m"));
      assertEquals(result, Quantity.of(0, "m"));
      assertTrue(ExactScalarQ.of(result));
    }
    assertEquals(clipzone.apply(Quantity.of(0.2, "m")), Quantity.of(0, "m"));
    {
      Scalar result = clipzone.apply(Quantity.of(RationalScalar.HALF, "m"));
      assertEquals(result, Quantity.of(0, "m"));
      assertTrue(ExactScalarQ.of(result));
    }
    {
      Scalar result = clipzone.apply(Quantity.of(RationalScalar.of(3, 2), "m"));
      assertEquals(result, Quantity.of(RationalScalar.of(5, 4), "m"));
      assertTrue(ExactScalarQ.of(result));
    }
    {
      Scalar result = clipzone.apply(Quantity.of(RationalScalar.of(-3, 2), "m"));
      assertEquals(result, Quantity.of(RationalScalar.of(5, -4), "m"));
      assertTrue(ExactScalarQ.of(result));
    }
  }
}
