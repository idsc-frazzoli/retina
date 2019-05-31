// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class RimoGetEventTest extends TestCase {
  public void testSimple() {
    assertEquals(RimoGetEvent.LENGTH, 48);
  }

  public void testConstructor() {
    RimoGetEvent rge = RimoGetEvents.create(600, 300);
    assertEquals(rge.length(), 48);
    rge.asArray();
    Tensor pair = rge.getAngularRate_Y_pair();
    assertTrue(Sign.isPositive(pair.Get(1)));
    assertEquals(pair.Get(0), pair.Get(1).multiply(RealScalar.of(2)));
    Tensor tensor = pair.map(Magnitude.PER_SECOND);
    VectorQ.requireLength(tensor, 2);
  }
}
