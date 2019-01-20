// code by az
package ch.ethz.idsc.gokart.core.man;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.sca.Mod;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sca.Sin;

/** https://en.wikipedia.org/wiki/Chirp */
/* package */ class ChirpSignal implements ScalarUnaryOperator {
  private final Mod mod;
  private final ScalarUnaryOperator series;

  public ChirpSignal(double f0, double k, double T) {
    mod = Mod.function(T);
    series = Series.of(Tensors.vector(0, 2 * Math.PI * f0, k * Math.PI));
  }

  @Override // from ScalarUnaryOperator
  public Scalar apply(Scalar time) {
    return Sin.of(series.apply(mod.apply(time)));
  }
}
