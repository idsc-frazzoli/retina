// code by jph
package ch.ethz.idsc.gokart.core.man;

import java.util.Map;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Tally;
import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testSimple() {
    Tensor vector = StaticHelper.incrSteps(2);
    Map<Tensor, Long> map = Tally.of(vector);
    assertEquals(map.size(), 7);
    assertEquals(vector.length(), 3 * 2 * (2 + 1));
    assertEquals(map.get(DoubleScalar.of(0)), (Long) 12L);
  }
}
