// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public enum VectorNoise {
  ;
  // ---
  public static Scalar at(Tensor tensor) {
    switch (tensor.length()) {
    case 2:
      return RealScalar.of(SimplexNoise.at( //
          tensor.Get(0).number().doubleValue(), //
          tensor.Get(1).number().doubleValue()));
    case 3:
      return RealScalar.of(SimplexNoise.at( //
          tensor.Get(0).number().doubleValue(), //
          tensor.Get(1).number().doubleValue(), //
          tensor.Get(2).number().doubleValue()));
    default:
      break;
    }
    throw new RuntimeException();
  }
}
