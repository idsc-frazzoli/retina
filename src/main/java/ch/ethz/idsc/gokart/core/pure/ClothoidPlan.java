// code by gjoel
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.owl.math.planar.ClothoidPursuit;
import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.sophus.group.Se2GroupElement;
import ch.ethz.idsc.sophus.planar.SignedCurvature2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Norm;

public class ClothoidPlan {
  private static final int REFINEMENT = 6;

  /** @param lookAhead {x[m], y[m], angle}
   * @param pose of vehicle {x[m], y[m], angle}
   * @param isForward driving direction, true when forward or stopped, false when driving backwards
   * @return ClothoidPlan */
  public static Optional<ClothoidPlan> from(Tensor lookAhead, Tensor pose, boolean isForward) {
    ClothoidPursuit clothoidPursuit = new ClothoidPursuit(lookAhead);
    Tensor curveSE2 = ClothoidPursuit.curve(lookAhead, REFINEMENT);
    if (!isForward)
      CurveClothoidPursuitHelper.mirrorAndReverse(curveSE2);
    Tensor curve = Tensor.of(curveSE2.stream().map(new Se2GroupElement(pose)::combine));
    return Optional.of(new ClothoidPlan(clothoidPursuit.ratios(), curve));
  }

  // ---
  private final Tensor ratios;
  private final Tensor curve;

  /** @param ratios [m^-1] start and end, used to derive future heading in good precision
   * @param curve sparse planned to be followed */
  private ClothoidPlan(Tensor ratios, Tensor curve) {
    this.ratios = ratios;
    this.curve = curve;
  }

  public Tensor ratios() {
    return ratios.unmodifiable();
  }

  public Scalar ratio() {
    return ratio(0);
  }

  /** @param idx
   * @return ratio [m^-1] */
  public Scalar ratio(int idx) {
    if (idx == 0)
      return ratios.Get(0);
    if (idx == curve.length() - 1)
      return ratios.Get(1);
    Tensor curve2D = Tensor.of(curve.stream().map(Extract2D.FUNCTION));
    return SignedCurvature2D.of(curve2D.get(idx - 1), curve2D.get(idx), curve2D.get(idx + 1)).get();
  }

  /** @param pose of vehicle {x[m], y[m], angle}
   * @return ratio [m^-1] */
  public Scalar bestRatio(Tensor pose) {
    int idx = ArgMin.of(Tensor.of(curve.stream().map(pose::subtract).map(Extract2D.FUNCTION).map(Norm._2::ofVector)));
    return ratio(idx);
  }

  public Tensor curve() {
    return curve;
  }
}