// code by jph
package ch.ethz.idsc.gokart.offline.video;

import java.util.Objects;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Mod;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ class LaptimePoint {
  private final ScalarUnaryOperator scalarUnaryOperator;
  public final Tensor position;
  private boolean fused = false;
  private Scalar prev = null;
  private Scalar lap = Quantity.of(DoubleScalar.POSITIVE_INFINITY, SI.SECOND);

  public LaptimePoint(Scalar pathProgress, Tensor position, int length) {
    this.position = position;
    Mod mod = Mod.function(length);
    scalarUnaryOperator = scalar -> mod.apply(scalar.subtract(pathProgress));
  }

  public void digest(Scalar time, Scalar pathProgress) {
    Scalar result = scalarUnaryOperator.apply(pathProgress);
    fused |= Scalars.lessThan(RealScalar.ONE, result);
    if (fused) {
      if (Scalars.lessThan(result, RealScalar.of(0.5))) {
        if (Objects.nonNull(prev)) {
          lap = time.subtract(prev);
        }
        prev = time;
        fused = false;
      }
    }
  }

  public Scalar lap() {
    return lap;
  }
}
