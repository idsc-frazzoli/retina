// code by jph
package ch.ethz.idsc.retina.util.math;

import java.util.List;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.sca.Round;

enum DeprecatedUniformResampleDemo {
  ;
  public static void main(String[] args) {
    DeprecatedUniformResample deprecatedUniformResample = //
        new DeprecatedUniformResample(RealScalar.of(33), RealScalar.of(.3));
    List<Tensor> total = deprecatedUniformResample.apply( //
        Tensors.fromString("{{100, 0}, {100, 2}, {100, 3}, {10, 10}, {10, 10.2}, {10, 10.4}, {20, 40}}"));
    for (Tensor ret : total)
      System.out.println(Pretty.of(ret.map(Round._1)));
  }
}
