// code by jph
package ch.ethz.idsc.retina.util.curve;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class PeriodicExtractTest extends TestCase {
  public void testSimple() {
    PeriodicExtract pe = new PeriodicExtract(Tensors.vector(5, 3, 4, 2, 7));
    assertEquals(pe.get(-1), RealScalar.of(7));
    assertEquals(pe.get(0), RealScalar.of(5));
    assertEquals(pe.get(5), RealScalar.of(5));
    assertEquals(pe.get(6), RealScalar.of(3));
  }
}
