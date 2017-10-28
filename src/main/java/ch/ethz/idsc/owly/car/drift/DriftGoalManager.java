// code by edo
package ch.ethz.idsc.owly.car.drift;

import java.util.List;

import ch.ethz.idsc.owly.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owly.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owly.glc.core.GlcNode;
import ch.ethz.idsc.owly.glc.core.GoalInterface;
import ch.ethz.idsc.owly.math.flow.Flow;
import ch.ethz.idsc.owly.math.region.Region;
import ch.ethz.idsc.owly.math.state.StateTime;
import ch.ethz.idsc.owly.math.state.TimeInvariantRegion;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class DriftGoalManager extends SimpleTrajectoryRegionQuery implements GoalInterface {
  public static GoalInterface createStandard(Tensor goalState, Tensor tolerance) {
    return new DriftGoalManager(new DriftGoalRegion(goalState, tolerance));
  }

  // ---
  private DriftGoalManager(Region region) {
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
