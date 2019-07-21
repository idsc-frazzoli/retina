// code by gjoel
package ch.ethz.idsc.gokart.core.pure;

import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.alg.VectorQ;

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
}