// code by jph
package ch.ethz.idsc.retina.alg.slam;

import java.util.Arrays;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import junit.framework.TestCase;

public class Se2MultiresSamplesTest extends TestCase {
  public void testSimple() {
    Se2MultiresSamples se2MultiresSamples = Se2MultiresSamples.createDefault();
    Tensor list = se2MultiresSamples.level(2);
    assertEquals(Dimensions.of(list), Arrays.asList(27, 3, 3));
  }
}
