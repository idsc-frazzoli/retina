// code by gjoel
package ch.ethz.idsc.gokart.core.pure;

import java.util.List;
import java.util.function.Predicate;

import ch.ethz.idsc.owl.bot.se2.glc.DynamicRatioLimit;
import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.red.Norm;

// TODO JPH rename
/* package */ enum CurveClothoidPursuitHelper {
  ;
  /** mirror the points along the y axis and invert their orientation
   * @param se2points curve given by points {x,y,a} */
  public static void mirrorAndReverse(Tensor se2points) {
    if (VectorQ.of(se2points)) {
      se2points.set(Scalar::negate, 0);
      se2points.set(Scalar::negate, 2);
    } else {
      se2points.set(Scalar::negate, Tensor.ALL, 0);
      se2points.set(Scalar::negate, Tensor.ALL, 2);
    }
  }

  /** @param ratioLimits depending on pose and speed
   * @param pose of vehicle {x[m], y[m], angle}
   * @param speed of vehicle [m*s^-1]
   * @return predicate to determine whether ratio is compliant with all posed turning ratio limits */
  public static Predicate<Scalar> isCompliant(List<DynamicRatioLimit> ratioLimits, Tensor pose, Scalar speed) {
    return ratio -> ratioLimits.stream() //
        .map(dynamicRatioLimit -> dynamicRatioLimit.at(pose, speed)) //
        .allMatch(dynamicRatioLimit -> dynamicRatioLimit.isInside(ratio));
  }

  /** @param segment in se2
   * @param reference in se2
   * @return sum of distances from segment points to closest reference point */
  public static Scalar error(Tensor segment, Tensor reference) {
    TensorScalarFunction error = se2 -> //
        reference.stream().parallel().map(se2::subtract).map(Extract2D.FUNCTION).map(Norm._2::ofVector).reduce(Min::of).get();
    return segment.stream().parallel().map(error).reduce(Scalar::add).get();
  }
}