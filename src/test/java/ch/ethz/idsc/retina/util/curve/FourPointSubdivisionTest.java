// code by jph
package ch.ethz.idsc.retina.util.curve;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.red.Nest;
import junit.framework.TestCase;

public class FourPointSubdivisionTest extends TestCase {
  public void testSimple() {
    Tensor curve = Tensors.fromString("{{0,0},{1,0},{0,1}}");
    CurveSubdivision fps = new CurveSubdivision(FourPointSubdivision.SCHEME);
    Tensor n1 = Nest.of(fps, curve, 1);
    assertEquals(n1.get(0), Array.zeros(2));
    assertEquals(n1.get(1), Tensors.fromString("{9/16, -1/8}"));
    assertEquals(n1.get(2), UnitVector.of(2, 0));
    assertEquals(n1.get(3), Tensors.fromString("{9/16, 9/16}"));
  }
}
