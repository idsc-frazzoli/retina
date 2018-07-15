// code by edo
package ch.ethz.idsc.owl.car.drift;

import java.util.List;

import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TimeInvariantRegion;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

class DriftGoalManager extends SimpleTrajectoryRegionQuery implements GoalInterface {
  public static GoalInterface createStandard(Tensor goalState, Tensor tolerance) {
    return new DriftGoalManager(new DriftGoalRegion(goalState, tolerance));
  }

  // ---
  private DriftGoalManager(Region<Tensor> region) {
    super(new TimeInvariantRegion(region));
  }

  @Override
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    return StateTimeTrajectories.timeIncrement(glcNode, trajectory);
  }

  @Override
  public Scalar minCostToGoal(Tensor x) {
    return RealScalar.ZERO;
  }
}
