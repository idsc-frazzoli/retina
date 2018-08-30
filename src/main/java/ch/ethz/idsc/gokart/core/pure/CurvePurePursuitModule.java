// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.planar.PurePursuit;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public final class CurvePurePursuitModule extends PurePursuitModule implements GokartPoseListener {
  private Optional<Tensor> optionalCurve = Optional.empty();

  // TODO JPH function should return a scalar with unit "rad*m^-1"...
  // right now, "curve" does not have "m" as unit but entries are unitless.
  /* package */ Optional<Scalar> getRatio(Tensor pose, boolean isForward) {
    Optional<Tensor> optionalCurve = this.optionalCurve; // copy reference instead of synchronize
    if (optionalCurve.isPresent()) {
      Tensor curve = optionalCurve.get();
      return getRatio(pose, curve, isForward);
    } else {
      System.err.println("no curve in pure pursuit");
    }
    return Optional.empty();
  }

  static Optional<Scalar> getRatio(Tensor pose, Tensor curve, boolean isForward) {
    TensorUnaryOperator toLocal = new Se2Bijection(GokartPoseHelper.toUnitless(pose)).inverse();
    Tensor tensor = Tensor.of(curve.stream().map(toLocal));
    if (!isForward) { // if measured tangent speed is negative
      tensor.set(Scalar::negate, Tensor.ALL, 0); // flip sign of X coord. of waypoints in tensor
      tensor = Reverse.of(tensor); // reverse order of points along trajectory
    }
    Scalar distance = PursuitConfig.GLOBAL.lookAheadMeter();
    Optional<Tensor> aheadTrail = CurveUtils.getAheadTrail(tensor, distance);
    if (aheadTrail.isPresent()) {
      PurePursuit purePursuit = PurePursuit.fromTrajectory(aheadTrail.get(), distance);
      return purePursuit.ratio();
    }
    return Optional.empty();
  }

  /** function for trajectory planner
   * 
   * @param curve */
  public void setCurve(Optional<Tensor> curve) {
    optionalCurve = curve;
  }

  /** @return curve */
  /* for tests */ Optional<Tensor> getCurve() {
    return optionalCurve;
  }
}
