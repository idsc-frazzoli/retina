// code by jph
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class BlindSpotsTest extends TestCase {
  public void testEmpty() {
    BlindSpots blindSpots = new BlindSpots(Tensors.empty());
    assertFalse(blindSpots.isBlind(null));
  }

  public void testSimple() {
    BlindSpots blindSpots = BlindSpots.defaultGokart();
    assertTrue(blindSpots.isBlind(RealScalar.of(0)));
    assertFalse(blindSpots.isBlind(RealScalar.of(1)));
    assertFalse(blindSpots.isBlind(RealScalar.of(2)));
    assertTrue(blindSpots.isBlind(RealScalar.of(3.1)));
    assertFalse(blindSpots.isBlind(RealScalar.of(4)));
    assertFalse(blindSpots.isBlind(RealScalar.of(1 + Math.PI)));
    assertFalse(blindSpots.isBlind(RealScalar.of(5)));
  }
}
