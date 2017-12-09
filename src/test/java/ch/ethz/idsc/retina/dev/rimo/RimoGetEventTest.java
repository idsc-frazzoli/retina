// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.nio.ByteBuffer;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class RimoGetEventTest extends TestCase {
  public void testSimple() {
    assertEquals(RimoGetEvent.LENGTH, 48);
  }

  public void testConstructor() {
    ByteBuffer bb = ByteBuffer.wrap(new byte[48]);
    bb.putShort(2, (short) -600);
    bb.putShort(2 + 24, (short) 300);
    RimoGetEvent rge = new RimoGetEvent(bb);
    assertEquals(rge.length(), 48);
    rge.asArray();
    Tensor pair = rge.getAngularRate_Y_pair();
    assertTrue(Sign.isPositive(pair.Get(1)));
    assertEquals(pair.Get(0), pair.Get(1).multiply(RealScalar.of(2)));
  }
}
