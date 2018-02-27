// code by jph
package ch.ethz.idsc.gokart.gui.top;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class PredefinedMapTest extends TestCase {
  public void testSimple() {
    PredefinedMap predefinedMap = PredefinedMap.DUBENDORF_HANGAR_20180122;
    Tensor range = predefinedMap.range();
    assertTrue(Chop._08.close(range, Tensors.vector(85.33333333333333, 85.33333333333333)));
  }
}
