// code by gjoel
package ch.ethz.idsc.gokart.core.plan;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Norm;

/* package */ enum StaticHelper {
  ;
  private static final Se2Wrap SE2WRAP = Se2Wrap.INSTANCE;

  /** @param trajectory
   * @param state
   * @return */
  static int locate(Collection<TrajectorySample> trajectory, Tensor state) {
    if (Objects.isNull(trajectory) || //
        trajectory.isEmpty()) {
      // FIXME GJOEL null pointer
      trajectory.forEach(System.err::println);
      throw TensorRuntimeException.of(state);
    }
    return locate(trajectory.stream().map(TrajectorySample::stateTime).map(StateTime::state), state);
  }

  /** @param waypoints
   * @param state
   * @return */
  static int locate(Tensor waypoints, Tensor state) {
    if (Objects.isNull(waypoints) || //
        Tensors.isEmpty(waypoints))
      throw TensorRuntimeException.of(state, waypoints);
    return locate(waypoints.stream(), state);
  }

  private static int locate(Stream<Tensor> stream, Tensor state) {
    // find closest waypoint to current position, exists since waypoints is non-null/-empty
    Tensor distances = Tensor.of(stream.map(wp -> Norm._2.ofVector(SE2WRAP.difference(wp, state))));
    return ArgMin.of(distances);
  }
}
