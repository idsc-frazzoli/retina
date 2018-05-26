// code by jph
package ch.ethz.idsc.demo.vc;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class PolygonAreaTest extends TestCase {
  public void testArea() {
    {
      Tensor poly = Tensors.fromString("{{1,1},{2,1},{1,2}}");
      assertEquals(PolygonArea.signed(poly), RationalScalar.HALF.number().doubleValue());
    }
    {
      Tensor poly = Tensors.fromString("{{1,1},{2,1},{2,2},{1,2}}");
      assertEquals(PolygonArea.signed(poly), RealScalar.ONE.number().doubleValue());
    }
    {
      Tensor poly = Tensors.fromString("{{1,1},{2,1}}");
      assertEquals(PolygonArea.signed(poly), RealScalar.ZERO.number().doubleValue());
    }
    {
      Tensor poly = Tensors.fromString("{{1,1}}");
      assertEquals(PolygonArea.signed(poly), RealScalar.ZERO.number().doubleValue());
    }
  }
}
