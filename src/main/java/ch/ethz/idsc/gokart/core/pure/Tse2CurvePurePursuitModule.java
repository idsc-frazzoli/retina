// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Norm;

public class Tse2CurvePurePursuitModule extends CurvePurePursuitModule {
  private List<TrajectorySample> trajectory;

  public void setCurveTse2(List<TrajectorySample> trajectory) {
    Tensor curve = Tensor.of(trajectory.stream() //
        .map(trajectorySample -> trajectorySample.stateTime().state().extract(0, 2)));
    setCurve(Optional.of(curve));
    // ---
    this.trajectory = trajectory;
  }

  @Override
  protected Scalar getSpeedMultiplier() {
    // gokartPoseEvent is non-null at this point
    Tensor pose = gokartPoseEvent.getPose(); // latest pose
    TensorUnaryOperator toLocal = new Se2Bijection(GokartPoseHelper.toUnitless(pose)).inverse();
    // optionalCurve should be present at this point
    Tensor tensor = Tensor.of(optionalCurve.get().stream().map(toLocal).map(Norm._2::ofVector));
    // tensor should not be empty
    int index = ArgMin.of(tensor);
    TrajectorySample trajectorySample = trajectory.get(index);
    return trajectorySample.stateTime().state().Get(3).divide(GokartTrajectorySRModule.MAX_SPEED);
  }
}
