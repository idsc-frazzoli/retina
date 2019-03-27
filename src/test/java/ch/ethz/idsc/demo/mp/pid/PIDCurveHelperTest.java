//code by mcp
package ch.ethz.idsc.demo.mp.pid;

import ch.ethz.idsc.gokart.core.pure.DubendorfCurve;
import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class PIDCurveHelperTest extends TestCase {
  private final Tensor curve = Tensor.of(DubendorfCurve.TRACK_OVAL.stream().map(Extract2D.FUNCTION)).unmodifiable();
  private final Tensor pose = Tensors.fromString("{30[m], 40[m], 1}").unmodifiable();

  public void testClosest() {
    // FIXME MCP make curve with units [m]
    curve.copy();
    pose.copy();
    // int index = PIDCurveHelper.closest(curve, pose);
    // System.out.println(index);
  }

  public void testTrajAngle() {
    // FIXME MCP check by hand if done correctly
    // Tensor angle = PIDCurveHelper.trajAngle(curve, pose);
    // System.out.println(angle);
  }
}
