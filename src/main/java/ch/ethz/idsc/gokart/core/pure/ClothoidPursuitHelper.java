// code by gjoel
package ch.ethz.idsc.gokart.core.pure;

import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.red.Norm;

// TODO JPH rename
/* package */ enum ClothoidPursuitHelper {
  ;
  /** mirror the points along the y axis and invert their orientation
   * @param se2points curve given by points {x, y, a} */
  public static void mirrorAndReverse(Tensor se2points) {
    if (VectorQ.of(se2points)) {
      se2points.set(Scalar::negate, 0);
      se2points.set(Scalar::negate, 2);
    } else {
      se2points.set(Scalar::negate, Tensor.ALL, 0);
      se2points.set(Scalar::negate, Tensor.ALL, 2);
      Tensor reverse = Reverse.of(se2points);
      IntConsumer swap = i -> se2points.set(reverse.get(i), i);
      IntStream.range(0, se2points.length()).forEach(swap);
    }
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