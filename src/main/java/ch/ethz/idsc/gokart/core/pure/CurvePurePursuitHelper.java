// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.planar.PurePursuit;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ enum CurvePurePursuitHelper {
  ;
  /** @param pose of vehicle
   * @param curve in world coordinates with points of the form {x[m], y[m], [...]}
   * @param isForward driving direction, true when forward or stopped, false when driving backwards
   * @param distance for instance PursuitConfig.GLOBAL.lookAhead with unit [m]
   * @return ratio rate [rad*m^-1] */
  static Optional<Scalar> getRatio(Tensor pose, Tensor curve, boolean isForward, Scalar distance) {
    return getRatio(pose, curve, true, isForward, distance);
  }

  /** @param pose
   * @param curve in world coordinates with points of the form {x[m], y[m], [...]}
   * @param closed
   * @param isForward
   * @param distance
   * @return */
  static Optional<Scalar> getRatio(Tensor pose, Tensor curve, boolean closed, boolean isForward, Scalar distance) {
    TensorUnaryOperator toLocal = new Se2Bijection(pose).inverse();
    Tensor tensor = Tensor.of(curve.stream().map(toLocal));
    if (!isForward) { // if measured tangent speed is negative
      tensor.set(Scalar::negate, Tensor.ALL, 0); // flip sign of X coord. of waypoints in tensor
      tensor = Reverse.of(tensor); // reverse order of points along trajectory
    }
    Optional<Tensor> aheadTrail = CurveUtils.getAheadTrail(tensor, distance);
    if (aheadTrail.isPresent()) {
      PurePursuit purePursuit = PurePursuit.fromTrajectory(aheadTrail.get(), distance);
      return purePursuit.ratio();
    }
    return Optional.empty();
  }
}
