// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class HtmlLogReportTest extends TestCase {
  public void testSimple() {
    Tensor tensor = Tensors.empty().get(Tensor.ALL, 0);
    assertTrue(Tensors.isEmpty(tensor));
  }

  public void testSimple4() {
    Tensor tensor = Tensors.empty().get(Tensor.ALL, 4);
    assertTrue(Tensors.isEmpty(tensor));
  }
}
