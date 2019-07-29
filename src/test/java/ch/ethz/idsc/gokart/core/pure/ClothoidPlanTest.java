// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class ClothoidPlanTest extends TestCase {
  public void testForward() {
    Tensor pose = PoseHelper.attachUnits(Tensors.vector(-1, 3, 3));
    Tensor lookAhead = PoseHelper.attachUnits(Tensors.vector(1, 2, 3));
    Optional<ClothoidPlan> optional = ClothoidPlan.from(lookAhead, pose, true);
    ClothoidPlan clothoidPlan = optional.get();
    Clips.interval(0.3, 0.31).requireInside(Magnitude.PER_METER.apply(clothoidPlan.ratio()));
    Tensor goal = new Se2GroupElement(pose).combine(lookAhead);
    assertEquals(pose, clothoidPlan.curve().get(0));
    assertEquals(goal, clothoidPlan.curve().get(clothoidPlan.curve().length() - 1));
  }

  public void testReverse() {
    Tensor pose = PoseHelper.attachUnits(Tensors.vector(10, 500, 2));
    Tensor lookAhead = PoseHelper.attachUnits(Tensors.vector(1, 2, 3)); // imagine this being seen through rear mirror
    Optional<ClothoidPlan> optional = ClothoidPlan.from(lookAhead, pose, false);
    ClothoidPlan clothoidPlan = optional.get();
    Clips.interval(0.3, 0.31).requireInside(Magnitude.PER_METER.apply(clothoidPlan.ratio()));
    ClothoidPursuitHelper.mirrorAndReverse(lookAhead);
    Tensor goal = new Se2GroupElement(pose).combine(lookAhead);
    assertEquals(pose, clothoidPlan.curve().get(clothoidPlan.curve().length() - 1));
    assertEquals(goal, clothoidPlan.curve().get(0));
  }
}
