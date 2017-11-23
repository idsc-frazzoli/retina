// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.tensor.Parallelize;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

public enum TransposeDotDemo {
  ;
  @SuppressWarnings("unused")
  public static void main(String[] args) {
    Tensor s = RandomVariate.of(UniformDistribution.unit(), 30000, 3);
    Tensor st = Transpose.of(s);
    Tensor a = RandomVariate.of(UniformDistribution.unit(), 3, 3);
    {
      Tensor.of(s.stream().map(r -> a.dot(r)));
      Stopwatch stopwatch = Stopwatch.started();
      Tensor b = Tensor.of(s.stream().map(r -> a.dot(r)));
      long duration = stopwatch.display_nanoSeconds();
      System.out.println(duration);
    }
    {
      s.dot(a);
      Stopwatch stopwatch = Stopwatch.started();
      Tensor b = s.dot(a);
      // Parallelize.dot(s, a);
      long duration = stopwatch.display_nanoSeconds();
      System.out.println(duration);
    }
    {
      Stopwatch stopwatch = Stopwatch.started();
      // Tensor b = s.dot(a);
      Tensor b = Parallelize.dot(s, a);
      long duration = stopwatch.display_nanoSeconds();
      // System.out.println(duration);
    }
    {
      Stopwatch stopwatch = Stopwatch.started();
      // Tensor b = s.dot(a);
      Tensor b = Parallelize.dot(s, a);
      long duration = stopwatch.display_nanoSeconds();
      System.out.println(duration);
    }
  }
}
