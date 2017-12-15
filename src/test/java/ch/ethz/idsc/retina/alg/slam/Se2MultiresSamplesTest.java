// code by jph
package ch.ethz.idsc.retina.alg.slam;

import java.util.Arrays;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.Pretty;
import junit.framework.TestCase;

public class Se2MultiresSamplesTest extends TestCase {
  public void testSimple() {
    Se2MultiresSamples se2MultiresSamples = new Se2MultiresSamples( //
        RealScalar.of(0.03), //
        RealScalar.of(2 * 3.14 / 180), //
        3);
    Tensor list = se2MultiresSamples.level(2);
    assertEquals(Dimensions.of(list), Arrays.asList(27, 3, 3));
    assertEquals(se2MultiresSamples.levels(), 3);
  }

  public static void main(String[] args) {
    Se2MultiresSamples se2MultiresSamples = new Se2MultiresSamples( //
        RealScalar.of(0.03), //
        RealScalar.of(2 * 3.14 / 180), //
        3);
    for (Tensor matrix : se2MultiresSamples.level(0))
      System.out.println(Pretty.of(matrix));
  }
}
