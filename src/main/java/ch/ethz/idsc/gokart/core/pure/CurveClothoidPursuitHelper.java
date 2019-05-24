// code by gjoel
package ch.ethz.idsc.gokart.core.pure;

import java.util.List;
import java.util.function.Predicate;

import ch.ethz.idsc.owl.bot.se2.glc.DynamicRatioLimit;
import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Norm;

// TODO JPH rename
public enum CurveClothoidPursuitHelper {
  ;
  /** mirror the points along the y axis and invert their orientation
   * @param se2points curve given by points {x,y,a} */
  /* package */ static void mirrorAndReverse(Tensor se2points) {
    se2points.set(Scalar::negate, Tensor.ALL, 0);
    se2points.set(Scalar::negate, Tensor.ALL, 2);
  }

  /** @param ratioLimits depending on pose and speed
   * @param pose of vehicle {x[m], y[m], angle}
   * @param speed of vehicle [m*s^-1]
   * @return predicate to determine whether ratio is compliant with all posed turning ratio limits */
  /* package */ static Predicate<Scalar> isCompliant(List<DynamicRatioLimit> ratioLimits, Tensor pose, Scalar speed) {
    return ratio -> ratioLimits.stream() //
        .map(dynamicRatioLimit -> dynamicRatioLimit.at(pose, speed)) //
        .allMatch(dynamicRatioLimit -> dynamicRatioLimit.isInside(ratio));
  }

  /** @param curve geodesic
   * @return approximated length of curve */
  /* package */ static Scalar curveLength(Tensor curve) {
    Tensor curve_ = Tensor.of(curve.stream().map(Extract2D.FUNCTION));
    int n = curve_.length();
    return curve_.extract(1, n).subtract(curve_.extract(0, n - 1)).stream() //
        .map(Norm._2::ofVector) //
        .reduce(Scalar::add).get();
  }

  /** @param clothoidPlan
   * @param pose of vehicle {x[m], y[m], angle}
   * @return [m] */
  /* package */ static Scalar distanceDriven(ClothoidPlan clothoidPlan, Tensor pose) {
    Tensor trajectory = clothoidPlan.curve();
    int idx = ArgMin.of(Tensor.of(trajectory.stream().map(pose::subtract).map(Extract2D.FUNCTION).map(Norm._2::ofVector)));
    return idx > 0 ? curveLength(trajectory.extract(0, idx + 1)) : Quantity.of(0, SI.METER);
  }

  /** @param clothoidPlan
   * @param pose of vehicle {x[m], y[m], angle}
   * @return [m] */
  /* package */ static Scalar distanceToDrive(ClothoidPlan clothoidPlan, Tensor pose) {
    Tensor trajectory = clothoidPlan.curve();
    int idx = ArgMin.of(Tensor.of(trajectory.stream().map(pose::subtract).map(Extract2D.FUNCTION).map(Norm._2::ofVector)));
    return idx < trajectory.length() - 1 ? curveLength(trajectory.extract(idx, trajectory.length())) : Quantity.of(0, SI.METER);
  }
}