// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

public enum CurveUtils {
  ;
  public static final int NO_MATCH = -1;

  public static int closestCloserThan(Tensor tensor, Scalar dist) {
    int best = NO_MATCH;
    for (int index = 0; index < tensor.length(); ++index) {
      Scalar norm = Norm._2.of(tensor.get(index)); // vector in local coordinates
      if (Scalars.lessThan(norm, dist)) {
        dist = norm;
        best = index;
      }
    }
    return best;
  }

  public static Optional<Tensor> getAheadTrail(Tensor tensor, Scalar dist) {
    int index = closestCloserThan(tensor, dist);
    if (index != NO_MATCH) {
      int length = tensor.length();
      return Optional.of(Tensor.of( //
          IntStream.range(index, index + length / 2) // at most half of the curve
              .map(i -> i % length) //
              .mapToObj(tensor::get)));
    }
    return Optional.empty();
  }
}
