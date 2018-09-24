// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Norm;

/** implementation is similar to CurvePurePursuitModule with the additional
 * feature that the trajectory is annotated with velocity */
public final class Tse2CurvePurePursuitModule extends CurvePurePursuitModule {
  private final Object lock = new Object();
  private List<TrajectorySample> trajectory;

  public Tse2CurvePurePursuitModule(PursuitConfig pursuitConfig) {
    super(pursuitConfig);
  }

  public void setCurveTse2(List<TrajectorySample> trajectory) {
    Tensor curve = Tensor.of(trajectory.stream() //
        .map(TrajectorySample::stateTime) //
        .map(StateTime::state) //
        .map(tensor -> tensor.extract(0, 2)));
    synchronized (lock) {
      setCurve(Optional.of(curve));
      this.trajectory = trajectory;
    }
  }

  @Override // from PurePursuitModule
  protected Scalar getSpeedMultiplier() {
    // gokartPoseEvent is non-null at this point, implied by
    // PurePursuitModule.runAlgo, and CurvePurePursuitModule.deriveHeading
    Tensor pose = gokartPoseEvent.getPose(); // latest pose
    TensorUnaryOperator toLocal = new Se2Bijection(GokartPoseHelper.toUnitless(pose)).inverse();
    synchronized (lock) {
      Optional<Tensor> curve = getCurve();
      if (curve.isPresent()) {
        Tensor tensor = Tensor.of(curve.get().stream().map(toLocal).map(Norm._2::ofVector));
        // tensor should not be empty
        int index = ArgMin.of(tensor);
        TrajectorySample trajectorySample = trajectory.get(index);
        return trajectorySample.stateTime().state().Get(3).divide(GokartTrajectorySRModule.MAX_SPEED);
      }
    }
    return RealScalar.ZERO;
  }
}
