// code by jph
package ch.ethz.idsc.gokart.core.perc;

import java.util.Set;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class DbscanTest extends TestCase {
  public void testSimple() {
    double[][] data = new double[4][2];
    data[0][0] = 0.99;
    data[0][1] = 0.16;
    data[1][0] = 2.1;
    data[1][1] = 3.98;
    data[2][0] = 2.16;
    data[2][1] = 3.99;
    data[3][0] = 0.98;
    data[3][1] = 0.1;
    Tensor p = Tensors.matrixDouble(data);
    Tensor clusters = Dbscan.of(p, 0.2, 2);
    assertEquals(clusters.length(), 2);
    Set<Tensor> set = clusters.stream().collect(Collectors.toSet());
    assertTrue(set.contains(Tensors.fromString("{{2.1, 3.98}, {2.16, 3.99}}")));
    assertTrue(set.contains(Tensors.fromString("{{0.99, 0.16}, {0.98, 0.1}}")));
  }
}
