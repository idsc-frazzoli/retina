// code by mcp
package ch.ethz.idsc.demo.mp.pid;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class Se2CurveUnitCheckTest extends TestCase {
  public void testCurveTrue() {
    Tensor curveUnit = Tensors.vector( //
        i -> Tensors.of(Quantity.of(i, SI.METER), Quantity.of(1, SI.METER), Pi.HALF), 100);
    boolean cond = Se2CurveUnitCheck.that(curveUnit, SI.METER);
    assertEquals(cond, true);
  }

  public void testCurveFalse() {
    Tensor curveUnitless = Tensors.vector( //
        i -> Tensors.of(RealScalar.of(i), RealScalar.ONE, Pi.HALF), 100);
    boolean cond = Se2CurveUnitCheck.that(curveUnitless, SI.METER);
    assertEquals(cond, false);
  }

  public void testCurveWrongUnit() {
    Tensor curveUnit = Tensors.vector( //
        i -> Tensors.of(Quantity.of(i, SI.METER), Quantity.of(1, SI.METER), Pi.HALF), 100);
    boolean cond = Se2CurveUnitCheck.that(curveUnit, SI.SECOND);
    assertEquals(cond, false);
  }

  public void testPoseTrue() {
    Tensor pose = Tensors.fromString("{30[m],40[m], 1.57}");
    boolean cond = Se2CurveUnitCheck.poseHasUnits(pose, SI.METER);
    assertEquals(cond, true);
  }

  public void testPoseFalse() {
    Tensor pose = Tensors.fromString("{30,40, 1.57}");
    boolean cond = Se2CurveUnitCheck.poseHasUnits(pose, SI.METER);
    assertEquals(cond, false);
  }

  public void testPoseWrongUnit() {
    Tensor pose = Tensors.fromString("{30[m],40[m], 1.57}");
    boolean cond = Se2CurveUnitCheck.poseHasUnits(pose, SI.SECOND);
    assertEquals(cond, false);
  }
}