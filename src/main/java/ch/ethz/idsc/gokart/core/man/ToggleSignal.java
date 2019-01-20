// code by jph
package ch.ethz.idsc.gokart.core.man;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.MappedInterpolation;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Mod;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

// TODO ToggleSignal seems to be a special case of VectorSignal
/* package */ class ToggleSignal implements ScalarUnaryOperator {
  private static final Tensor SIGNAL = Tensors.vector(1, 0, 0, -1, 0, 0).unmodifiable();
  private static final Mod MOD = Mod.function(SIGNAL.length());

  // ---
  /** @param width
   * @param amplitude in the unit interval [0, 1]
   * @return */
  public static ScalarUnaryOperator create(double width, double amplitude) {
    return new ToggleSignal( //
        RealScalar.of(width), //
        Clip.unit().requireInside(RealScalar.of(amplitude)));
  }

  // ---
  private final Interpolation interpolation;

  /** @param width of single bit
   * @param amplitude */
  ToggleSignal(Scalar width, Scalar amplitude) {
    Tensor vector = SIGNAL.multiply(amplitude);
    interpolation = MappedInterpolation.of(vector, tensor -> MOD.of(tensor.divide(width)));
  }

  @Override // from ScalarUnaryOperator
  public Scalar apply(Scalar scalar) {
    return interpolation.At(scalar);
  }
}
