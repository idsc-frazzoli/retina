// code by jph
package ch.ethz.idsc.retina.util.math;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.red.Norm;

/** initial implementation for resampling a sequence of irregular spaced points
 * 
 * UniformResample is superseded by {@link ParametricResample} */
/* package */ class UniformResample implements Serializable {
  /** determines whether points are connected */
  private final Scalar threshold;
  /** distance between samples after re-sampling */
  private final Scalar ds;
  public int minLength = 2;

  /** the threshold
   * 
   * @param threshold a common value is RealScalar.of(33)
   * @param ds distance between samples after re-sampling */
  public UniformResample(Scalar threshold, Scalar ds) {
    this.threshold = threshold;
    this.ds = ds;
  }

  /** @param points sequence of lidar points in ccw- or cw-direction
   * @return list of points grouped by connectivity and resampled equidistantly */
  List<Tensor> apply(Tensor points) {
    Tensor dista = Tensor.of(points.stream().map(Norm._2::ofVector));
    Tensor diffs = Differences.of(points);
    Tensor delta = Tensor.of(diffs.stream().map(Norm._2::ofVector));
    List<Tensor> total = new LinkedList<>();
    Tensor ret = Tensors.empty();
    Scalar sum = RealScalar.ZERO;
    for (int index = 0; index < diffs.length(); ++index) {
      boolean connected = Scalars.lessThan( //
          delta.Get(index).multiply(threshold), //
          dista.Get(index + 0).add(dista.Get(index + 1)));
      if (connected) {
        while (Scalars.lessThan(sum, delta.Get(index))) {
          Scalar factor = sum.divide(delta.Get(index));
          Tensor shift = diffs.get(index).multiply(factor);
          Tensor interp = points.get(index).add(shift);
          ret.append(interp);
          sum = sum.add(ds);
        }
        sum = sum.subtract(delta.Get(index));
      } else {
        if (addPredicate(ret))
          total.add(ret);
        ret = Tensors.empty();
        sum = RealScalar.ZERO;
      }
    }
    if (addPredicate(ret))
      total.add(ret);
    return total;
  }

  private boolean addPredicate(Tensor tensor) {
    return minLength <= tensor.length();
  }
}
