// code by jph
package ch.ethz.idsc.gokart.core.pure;

import ch.ethz.idsc.sophus.group.Se2GroupElement;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class CurveGeodesicPursuitHelperTest extends TestCase {
  public void testTransform() {
    Se2GroupElement se2GroupElement = new Se2GroupElement(Tensors.fromString("{2[m],3[m],1}"));
    Tensor curve = Tensors.fromString("{{2[m],3[m],1},{3[m],4[m],2}}");
    Tensor local = Tensor.of(curve.stream().map(se2GroupElement.inverse()::combine));
    System.out.println(local);
  }
}
