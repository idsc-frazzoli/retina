// code by jph
package ch.ethz.idsc.retina.util.curve;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Nest;
import junit.framework.TestCase;

public class FourPointSubdivisionTest extends TestCase {
  public void testSimple() {
    Tensor curve = Tensors.fromString("{{0,0},{1,0},{0,1}}");
    CurveSubdivision fps = new CurveSubdivision(FourPointSubdivision.SCHEME);
    Tensor n1 = Nest.of(fps, curve, 3);
    System.out.println(n1);
  }
}
