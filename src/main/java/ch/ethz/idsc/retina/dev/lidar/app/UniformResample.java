// code by jph
package ch.ethz.idsc.retina.dev.lidar.app;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Round;

public class UniformResample implements Serializable {
  /** determines whether points are connected */
  private final Scalar threshold;
  /** distance between samples after re-sampling */
  private final Scalar ds;

  /** the threshold
   * 
   * @param threshold a common value is RealScalar.of(33)
   * @param ds distance between samples after re-sampling */
  public UniformResample(Scalar threshold, Scalar ds) {
    this.threshold = threshold;
    this.ds = ds;
  }

  public List<Tensor> apply(Tensor points) {
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
        if (ret.length() != 0) {
          total.add(ret);
          ret = Tensors.empty();
        }
        sum = RealScalar.ZERO;
      }
    }
    if (ret.length() != 0)
      total.add(ret);
    return total;
  }

  public static void main(String[] args) {
    UniformResample pr = new UniformResample(RealScalar.of(33), RealScalar.of(.3));
    List<Tensor> total = pr.apply(Tensors.fromString("{{100,0},{100,2},{100,3},{10,10},{10,10.2},{10,10.4},{20,40}}"));
    for (Tensor ret : total)
      System.out.println(Pretty.of(ret.map(Round._1)));
  }
}
