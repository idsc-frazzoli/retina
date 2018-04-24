package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Multinomial;
import ch.ethz.idsc.tensor.sca.Mod;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sca.Sin;

public class ChirpSignal implements ScalarUnaryOperator {
  private final Mod MOD;
  private final Tensor coeffs;

  public ChirpSignal(double f0, double k, double T) {
    // TODO Auto-generated constructor stub
    MOD = Mod.function(T);
    coeffs = Tensors.vector(0, 2 * Math.PI * f0, k * Math.PI);
  }

  @Override
  public Scalar apply(Scalar time) {
    return Sin.of(Multinomial.horner(coeffs, MOD.apply(time)));
  }
}
