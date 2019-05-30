// code by mcp
package ch.ethz.idsc.demo.mp.pid;

import java.util.Arrays;

import ch.ethz.idsc.gokart.core.pure.DubendorfCurve;
import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import junit.framework.TestCase;

public class RnCurveHelperTest extends TestCase {
  private final Tensor curve = Tensor.of(DubendorfCurve.TRACK_OVAL_R2.stream().map(Extract2D.FUNCTION)).unmodifiable();
  // private final Tensor pose = Tensors.fromString("{30[m], 40[m], 1}").unmodifiable();

  public void testTrajAngle() {
    // FIXME MCP check by hand if done correctly
    assertEquals(Dimensions.of(curve), Arrays.asList(224, 2));
    Tensor curveAngle = RnCurveHelper.addAngleToCurve(curve);
    assertEquals(Dimensions.of(curveAngle), Arrays.asList(224, 3));
    for (int i = 0; i < curveAngle.length(); i++) {
      curveAngle.get(i).append(RealScalar.of(i));
      // System.out.println(curveAngle.Get(i, 2));
    }
  }

  public void testCirclePoints() {
    Tensor curveAngle = RnCurveHelper.addAngleToCurve(CirclePoints.of(10).unmodifiable());
    // System.out.println(curveAngle.get(Tensor.ALL,2));
  }

  public void testEmpty() {
    // TODO MCP
    RnCurveHelper.addAngleToCurve(Tensors.empty());
  }

  public void testSingleton() {
    RnCurveHelper.addAngleToCurve(Tensors.of(Tensors.vector(1, 2)));
    // TODO
  }

  public void testNull() {
    try {
      RnCurveHelper.addAngleToCurve(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
