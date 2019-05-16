// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class ClothoidPlanTest extends TestCase {
  public void testForward() {
    Optional<ClothoidPlan> optional = ClothoidPlan.from( //
        PoseHelper.attachUnits(Tensors.vector(1, 2, 3)), //
        PoseHelper.attachUnits(Tensors.vector(-1, 3, 3)), true);
    ClothoidPlan clothoidPlan = optional.get();
    clothoidPlan.curve().stream().forEach(PoseHelper::toUnitless);
    Clips.interval(0.07, 0.09).requireInside(Magnitude.PER_METER.apply(clothoidPlan.ratio()));
  }

  public void testReverse() {
    Optional<ClothoidPlan> optional = ClothoidPlan.from( //
        PoseHelper.attachUnits(Tensors.vector(1, 2, 3)), //
        PoseHelper.attachUnits(Tensors.vector(10, 500, 2)), false);
    ClothoidPlan clothoidPlan = optional.get();
    clothoidPlan.curve().stream().forEach(PoseHelper::toUnitless);
    // TODO strange values
    Clips.interval(0.07, 0.09).requireInside(Magnitude.PER_METER.apply(clothoidPlan.ratio()));
  }
}
