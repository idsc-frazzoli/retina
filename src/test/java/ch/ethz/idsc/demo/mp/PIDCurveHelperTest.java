//code by mcp
package ch.ethz.idsc.demo.mp;

import ch.ethz.idsc.demo.mp.pid.PIDCurveHelper;
import ch.ethz.idsc.gokart.core.pure.DubendorfCurve;
import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class PIDCurveHelperTest extends TestCase {
  private Tensor curve = Tensor.of(DubendorfCurve.TRACK_OVAL.stream().map(Extract2D.FUNCTION));
  private Tensor pose = Tensors.fromString("{30[m], 40[m], 1}");

  public void testClosest() {
    // TODO MCP make curve with units [m]
    int index = PIDCurveHelper.closest(curve, pose);
    System.out.println(index);
  }

  public void testTrajAngle() {
    // TODO MCP check by hand if done correctly
    Tensor angle = PIDCurveHelper.trajAngle(curve, pose);
    System.out.println(angle);
  }
}
