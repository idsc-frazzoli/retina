// code by az
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Multinomial;
import ch.ethz.idsc.tensor.sca.Mod;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sca.Sin;

/** https://en.wikipedia.org/wiki/Chirp */
public class ChirpSignal implements ScalarUnaryOperator {
  private final Mod mod;
  private final Tensor coeffs;

  public ChirpSignal(double f0, double k, double T) {
    mod = Mod.function(T);
    coeffs = Tensors.vector(0, 2 * Math.PI * f0, k * Math.PI);
  }

  @Override // from ScalarUnaryOperator
  public Scalar apply(Scalar time) {
    return Sin.of(Multinomial.horner(coeffs, mod.apply(time)));
  }
}
