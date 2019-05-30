// code by gjoel
package ch.ethz.idsc.gokart.core.pure;

import java.io.Serializable;
import java.util.Optional;

import ch.ethz.idsc.owl.math.planar.ClothoidPursuit;
import ch.ethz.idsc.sophus.group.Se2GroupElement;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class ClothoidPlan implements Serializable {
  private static final int REFINEMENT = 2;

  /** @param lookAhead {x[m], y[m], angle}
   * @param pose of vehicle {x[m], y[m], angle}
   * @param isForward driving direction, true when forward or stopped, false when driving backwards
   * @return ClothoidPlan */
  public static Optional<ClothoidPlan> from(Tensor lookAhead, Tensor pose, boolean isForward) {
    ClothoidPursuit clothoidPursuit = new ClothoidPursuit(lookAhead);
    Optional<Scalar> optional = clothoidPursuit.firstRatio(); // with unit [m^-1]
    // System.out.println("optional=" + optional);
    // return optional
    if (optional.isPresent()) {
      Scalar ratio = optional.get();
      Tensor curveSE2 = ClothoidPursuit.curve(lookAhead, REFINEMENT);
      if (!isForward)
        CurveClothoidPursuitHelper.mirrorAndReverse(curveSE2);
      Tensor curve = Tensor.of(curveSE2.stream().map(new Se2GroupElement(pose)::combine));
      return Optional.of(new ClothoidPlan(ratio, curve));
    }
    return Optional.empty();
  }

  // ---
  private final Scalar ratio;
  private final Tensor curve;

  /** @param ratio [m^-1] used to derive future heading in good precision
   * @param curve sparse planned to be followed */
  private ClothoidPlan(Scalar ratio, Tensor curve) {
    this.ratio = ratio;
    this.curve = curve;
  }

  public Scalar ratio() {
    return ratio;
  }

  public Tensor curve() {
    return curve;
  }
}