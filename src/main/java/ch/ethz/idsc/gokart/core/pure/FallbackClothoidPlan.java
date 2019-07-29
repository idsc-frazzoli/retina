// code by gjoel
package ch.ethz.idsc.gokart.core.pure;

import java.io.Serializable;
import java.util.Optional;

import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.sca.ArcTan;

public class FallbackClothoidPlan implements Serializable {
  /** @param pose of vehicle {x[m], y[m], angle}
   * @param ratio [m^-1]
   * @param isForward driving direction, true when forward or stopped, false when driving backwards
   * @return ClothoidPlan */
  public static Optional<ClothoidPlan> from(Tensor pose, Scalar ratio, boolean isForward) {
    Tensor base = Tensor.of(pose.stream().map(Tensor::Get).map(Scalar::zero));
    Scalar radius = ratio.reciprocal();
    base.set(radius, 1);
    Tensor curveSE2 = Tensor.of(CirclePoints.of(25).stream() //
        .map(t -> t.multiply(radius)) //
        .map(t -> t.append(ArcTan.of(t.Get(0), t.Get(1)).add(Pi.HALF))) //
        .map(base::add));
    if (!isForward)
      ClothoidPursuitHelper.mirrorAndReverse(curveSE2);
    Tensor curve = Tensor.of(curveSE2.stream().map(new Se2GroupElement(pose)::combine));
    return Optional.of(new ClothoidPlan(ratio, curve));
  }
}