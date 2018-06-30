// code by jph
package ch.ethz.idsc.retina.util.curve;

import java.io.IOException;

import ch.ethz.idsc.owl.subdiv.curve.EuclideanGeodesic;
import ch.ethz.idsc.owl.subdiv.curve.FourPointCurveSubdivision;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Nest;
import junit.framework.TestCase;

public class FourPointSubdivisionTest extends TestCase {
  public void testSimple() {
    Tensor curve = Tensors.fromString("{{0,0},{1,0},{0,1}}");
    TensorUnaryOperator subdivision = //
        new FourPointCurveSubdivision(EuclideanGeodesic.INSTANCE)::cyclic;
    Tensor n1 = Nest.of(subdivision, curve, 1);
    assertEquals(n1.get(0), Array.zeros(2));
    assertEquals(n1.get(1), Tensors.fromString("{9/16, -1/8}"));
    assertEquals(n1.get(2), UnitVector.of(2, 0));
    assertEquals(n1.get(3), Tensors.fromString("{9/16, 9/16}"));
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    TensorUnaryOperator fps = new FourPointCurveSubdivision(EuclideanGeodesic.INSTANCE)::cyclic;
    TensorUnaryOperator copy = Serialization.copy(fps);
    assertEquals(copy.apply(CirclePoints.of(10)), fps.apply(CirclePoints.of(10)));
  }
}
