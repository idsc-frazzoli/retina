// code by gjoel
package ch.ethz.idsc.gokart.core.pure;

import java.io.Serializable;
import java.util.Optional;

import ch.ethz.idsc.owl.math.pursuit.ClothoidPursuit;
import ch.ethz.idsc.owl.math.pursuit.ClothoidPursuits;
import ch.ethz.idsc.owl.math.pursuit.PursuitInterface;
import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class ClothoidPlan implements Serializable {
  // TODO JPH make configurable
  private static final int REFINEMENT = 5;

  /** @param lookAhead {x[m], y[m], angle} in vehicle coordinates
   * @param pose of vehicle {x[m], y[m], angle}
   * @param isForward driving direction, true when forward or stopped, false when driving backwards
   * @return ClothoidPlan */
  public static Optional<ClothoidPlan> from(Tensor lookAhead, Tensor pose, boolean isForward) {
    PursuitInterface pursuitInterface = ClothoidPursuit.of(lookAhead);
    Optional<Scalar> optional = pursuitInterface.firstRatio(); // with unit [m^-1]
    if (optional.isPresent()) {
      Scalar ratio = optional.get();
      Tensor curveSE2 = ClothoidPursuits.curve(lookAhead, REFINEMENT);
      if (!isForward)
        ClothoidPursuitHelper.mirrorAndReverse(curveSE2);
      Tensor curve = Tensor.of(curveSE2.stream().map(new Se2GroupElement(pose)::combine));
      return Optional.of(new ClothoidPlan(ratio, curve));
    }
    return Optional.empty();
  }

  // ---
  private final Scalar ratio;
  private final Tensor curve; // in directional order (not equivalent to driving order when in reverse)

  /** @param ratio [m^-1] used to derive future heading in good precision
   * @param curve sparse planned to be followed */
  private ClothoidPlan(Scalar ratio, Tensor curve) {
    this.ratio = ratio;
    this.curve = curve;
  }

  /** @return ratio (i.e. curvature) for driving along the begin of the clothoid */
  public Scalar ratio() {
    return ratio;
  }

  /** @return clothoid curve in global coordinates
   * in directional order (not equivalent to driving order when in reverse) */
  public Tensor curve() {
    return curve;
  }

  /** @return initial pose */
  public Tensor startPose() {
    return curve.get(0);
  }
}