// code by jph, gjoel
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.RimoAxleConstants;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class CurveClothoidPursuitPlannerTest extends TestCase {
  // TODO add more tests
  public void testSpecific1() throws Exception {
    Tensor pose = Tensors.fromString("{35.1[m], 44.9[m], 1}");
    Scalar speed = Quantity.of(1, SI.VELOCITY);
    Optional<Scalar> optional = new CurveClothoidPursuitPlanner().getPlan( //
        pose, speed, DubendorfCurve.TRACK_OVAL_SE2, true, //
        ClothoidPursuitConfig.ratioLimits()).map(ClothoidPlan::ratio);
    Scalar ratio = optional.get();
    Scalar angle = RimoAxleConstants.steerAngleForTurningRatio(ratio);
    // TODO
    // Clips.interval(-0.75, -0.72).requireInside(angle);
  }

  public void testSpecific2() throws Exception {
    Tensor pose = Tensors.fromString("{35.1[m], 44.9[m], 0.9}");
    Scalar speed = Quantity.of(1, SI.VELOCITY);
    Optional<Scalar> optional = new CurveClothoidPursuitPlanner().getPlan( //
        pose, speed, DubendorfCurve.TRACK_OVAL_SE2, true, //
        ClothoidPursuitConfig.ratioLimits()).map(ClothoidPlan::ratio);
    Scalar ratio = optional.get();
    Scalar angle = RimoAxleConstants.steerAngleForTurningRatio(ratio);
    // TODO
    // Clips.interval(-0.68, -0.62).requireInside(angle);
  }

  public void testTransform() {
    Se2GroupElement se2GroupElement = new Se2GroupElement(Tensors.fromString("{2[m], 3[m], 1}"));
    TensorUnaryOperator tensorUnaryOperator = se2GroupElement.inverse()::combine;
    Tensor curve = Tensors.fromString("{{2[m], 3[m], 1},{3[m], 4[m], 2}}");
    Tensor local = Tensor.of(curve.stream().map(tensorUnaryOperator));
    assertTrue(Chop.NONE.allZero(local.get(0)));
  }
}
