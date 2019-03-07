// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;
import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.planar.ArcTan2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.red.Norm;

/* package */ enum CurveUtils {
  ;
  public static final int NO_MATCH = -1;
  /** 28 is half of 42 therefore this is the answer (joke) */
  private static final Scalar ANGLE_LIMIT = Degree.of(28);

  /** @param curve_local with or without intersections
   * @param dist
   * @return TODO documentation */
  public static Optional<Tensor> getAheadTrail(Tensor curve_local, Scalar dist) {
    int index = closestParallelThan(curve_local, dist);
    if (index != NO_MATCH) {
      int length = curve_local.length();
      return Optional.of(Tensor.of( //
          IntStream.range(index, index + length / 2) // at most half of the curve
              .map(i -> i % length) //
              .mapToObj(curve_local::get)));
    }
    return Optional.empty();
  }

  /** @param curve_local in robot coordinates
   * @param dist
   * @return */
  /* package */ static int closestParallelThan(Tensor curve_local, Scalar dist) {
    int best = NO_MATCH;
    for (int index = 0; index < curve_local.length(); ++index) {
      final Tensor p0 = curve_local.get(index);
      Scalar norm = Norm._2.of(p0); // vector in local coordinates
      if (Scalars.lessThan(norm, dist)) {
        int next = index + 1;
        next %= curve_local.length();
        Tensor p1 = curve_local.get(next);
        Scalar angle = ArcTan2D.of(p1.subtract(p0));
        if (Scalars.lessThan(angle.abs(), ANGLE_LIMIT)) {
          dist = norm;
          best = index;
        }
      }
    }
    return best;
  }
}
