// code by jph
package ch.ethz.idsc.retina.util.data;

import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class ParamContainerTest extends TestCase {
  public void testSimple() {
    ParamContainer pc = ParamContainer.INSTANCE;
    assertTrue(pc.maxTor instanceof Quantity);
    assertEquals(pc.shape.length(), 4);
  }
}
