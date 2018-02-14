// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.sca.Round;

enum ParametricResampleDemo {
  ;
  public static void main(String[] args) {
    ParametricResample pr = new ParametricResample(RealScalar.of(33), RealScalar.of(.3));
    Tensor points = Tensors.fromString("{{100,0},{100,2},{100,3},{10,10},{10,10.2},{10,10.4},{20,40}}");
    ResampleResult resampleResult = pr.apply(points);
    for (Tensor ret : resampleResult.getParameters()) {
      System.out.println(ret.map(Round._2));
    }
    for (Tensor ret : resampleResult.getPoints())
      System.out.println(Pretty.of(ret.map(Round._1)));
  }
}
