// code by gjoel
package ch.ethz.idsc.demo.jg.following.sim;

import ch.ethz.idsc.gokart.core.pure.ClothoidPlan;
import ch.ethz.idsc.gokart.core.pure.ClothoidPursuitConfig;
import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.bot.se2.glc.CarHelper;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.sophus.group.Se2GroupElement;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class FollowingSimulationsTest extends TestCase {
  public void testMiniSimulation() {
    Chop chop = Chop._01;
    Tensor pose = FollowingSimulator.initialPose(Tensors.of(PoseHelper.attachUnits(Array.zeros(3))));
    Tensor lookAhead = FollowingSimulator.initialPose(Tensors.of(PoseHelper.attachUnits(Tensors.vector(3, 2, 1))));
    Tensor goal = new Se2GroupElement(pose).combine(lookAhead);
    Scalar speed = Quantity.of(5, SI.VELOCITY);
    Scalar ratio;
    ClothoidPlan plan = ClothoidPlan.from(lookAhead, pose,true).get();
    long time = System.currentTimeMillis();
    while (System.currentTimeMillis() < time + 1000 && !chop.close(goal, pose)) {
      ratio = plan.bestRatio(pose);
      pose = Se2CarIntegrator.INSTANCE.step(CarHelper.singleton(speed, ratio), pose, ClothoidPursuitConfig.GLOBAL.updatePeriod);
    }
    chop.requireClose(goal, pose);
  }
}
