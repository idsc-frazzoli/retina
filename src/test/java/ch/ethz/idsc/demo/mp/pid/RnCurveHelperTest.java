// code by mcp
package ch.ethz.idsc.demo.mp.pid;

import ch.ethz.idsc.gokart.core.pure.DubendorfCurve;
import ch.ethz.idsc.owl.bot.se2.pid.RnCurveHelper;
import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class RnCurveHelperTest extends TestCase {
  private final Tensor curve = Tensor.of(DubendorfCurve.TRACK_OVAL.stream().map(Extract2D.FUNCTION)).unmodifiable();
  private final Tensor pose = Tensors.fromString("{30[m], 40[m], 1}").unmodifiable();

  public void testClosest() {
    // FIXME MCP make curve with units [m]
    curve.copy();
    pose.copy();
    int index = RnCurveHelper.closest(curve, pose);
  }

  public void testTrajAngle() {
    // FIXME MCP check by hand if done correctly
    Tensor curveAngle = RnCurveHelper.addAngleToCurve(curve);
    for (int i = 0; i < curveAngle.length(); i++) {
      curveAngle.get(i).append(RealScalar.of(i));
      System.out.println(curveAngle.get(i).get(1));
    }
  }
  
  public void testTensorLib() {
    Tensor tensor = Tensors.fromString("{10,10}");
    Tensor tensor2 = Tensors.fromString("{20}");
    Scalar scalar = RealScalar.ONE;
    System.out.println(tensor);
    tensor.append(scalar);
    System.out.println(tensor);
    
  }
}
