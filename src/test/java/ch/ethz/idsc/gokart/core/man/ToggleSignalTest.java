// code by jph
package ch.ethz.idsc.gokart.core.man;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class ToggleSignalTest extends TestCase {
  public void testSimple() {
    ScalarUnaryOperator suo = ToggleSignal.create(10, .5);
    assertEquals(Subdivide.of(0.1, 9.9, 100).map(suo), Array.of(l -> RealScalar.of(.5), 101));
    assertEquals(Subdivide.of(10.1, 19.9, 100).map(suo), Array.of(l -> RealScalar.of(0), 101));
    assertEquals(Subdivide.of(20.1, 29.9, 100).map(suo), Array.of(l -> RealScalar.of(0), 101));
    assertEquals(Subdivide.of(30.1, 39.9, 100).map(suo), Array.of(l -> RealScalar.of(-.5), 101));
    assertEquals(Subdivide.of(40.1, 49.9, 100).map(suo), Array.of(l -> RealScalar.of(0), 101));
    assertEquals(Subdivide.of(50.1, 59.9, 100).map(suo), Array.of(l -> RealScalar.of(0), 101));
    assertEquals(Subdivide.of(60.1, 69.9, 100).map(suo), Array.of(l -> RealScalar.of(.5), 101));
    assertEquals(Subdivide.of(70.1, 79.9, 100).map(suo), Array.of(l -> RealScalar.of(0), 101));
  }
}
