// code by jph
package ch.ethz.idsc.demo.jph.macro;

import ch.ethz.idsc.tensor.NumberQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ enum SpeedClip implements ScalarUnaryOperator {
  FUNCTION;
  // ---
  private static final Clip CLIP = Clips.positive(6);

  @Override
  public Scalar apply(Scalar scalar) {
    return NumberQ.of(scalar) ? CLIP.rescale(scalar) : scalar;
  }
}