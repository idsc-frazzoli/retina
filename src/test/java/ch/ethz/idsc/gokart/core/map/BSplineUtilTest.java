// code by jph
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class BSplineUtilTest extends TestCase {
  public void testSimple() {
    Tensor tensor = BSplineUtil.getSidewardsUnitVectors(Tensors.vector(1, 2, 3, 4), Tensors.vector(3, 2, 2, 1), IdentityMatrix.of(4));
    Tensor matrix = Tensors.of( //
        Tensors.vector(0.9486832980505138, -0.31622776601683794), //
        Tensors.vector(0.7071067811865475, -0.7071067811865475), //
        Tensors.vector(0.5547001962252293, -0.8320502943378438), //
        Tensors.vector(0.24253562503633297, -0.9701425001453319));
    Chop._12.requireClose(tensor, matrix);
  }
}
