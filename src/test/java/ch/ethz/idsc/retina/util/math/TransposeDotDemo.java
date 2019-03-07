// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.Parallelize;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

public enum TransposeDotDemo {
  ;
  // @SuppressWarnings("unused")
  public static void main(String[] args) {
    Tensor s = RandomVariate.of(UniformDistribution.unit(), 30000, 3);
    Transpose.of(s);
    Tensor a = RandomVariate.of(UniformDistribution.unit(), 3, 3);
    {
      Tensor.of(s.stream().map(r -> a.dot(r)));
      Timing timing = Timing.started();
      Tensor.of(s.stream().map(r -> a.dot(r)));
      long duration = timing.nanoSeconds();
      System.out.println(duration);
    }
    {
      s.dot(a);
      Timing timing = Timing.started();
      s.dot(a);
      // Parallelize.dot(s, a);
      long duration = timing.nanoSeconds();
      System.out.println(duration);
    }
    {
      Timing timing = Timing.started();
      // Tensor b = s.dot(a);
      Parallelize.dot(s, a);
      long duration = timing.nanoSeconds();
      System.out.println(duration);
    }
    {
      Timing timing = Timing.started();
      // Tensor b = s.dot(a);
      Parallelize.dot(s, a);
      long duration = timing.nanoSeconds();
      System.out.println(duration);
    }
  }
}
