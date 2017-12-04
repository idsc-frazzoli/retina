// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class ClipzoneTest extends TestCase {
  public void testDeadzonePos() {
    Clipzone deadzone = new Clipzone(Clip.function(.1, 2));
    assertEquals(deadzone.apply(RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(deadzone.apply(RealScalar.of(.01)), RealScalar.ZERO);
    assertEquals(deadzone.apply(RealScalar.of(.1)), RealScalar.ZERO);
    assertTrue(Chop._14.close( //
        deadzone.apply(RealScalar.of(.2)), //
        RealScalar.of(0.10526315789473685)));
    assertEquals(deadzone.apply(RealScalar.of(2)), RealScalar.of(2));
    assertEquals(deadzone.apply(RealScalar.of(3)), RealScalar.of(2));
  }

  public void testDeadzoneNeg() {
    Clipzone deadzone = new Clipzone(Clip.function(.1, 2));
    assertEquals(deadzone.apply(RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(deadzone.apply(RealScalar.of(-.01)), RealScalar.ZERO);
    assertEquals(deadzone.apply(RealScalar.of(-.1)), RealScalar.ZERO);
    assertTrue(Chop._14.close( //
        deadzone.apply(RealScalar.of(-.2)), //
        RealScalar.of(-0.10526315789473685)));
    assertEquals(deadzone.apply(RealScalar.of(-2)), RealScalar.of(-2));
    assertEquals(deadzone.apply(RealScalar.of(-3)), RealScalar.of(-2));
  }
}
