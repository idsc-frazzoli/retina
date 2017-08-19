// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.alg.Flatten;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.io.ResourceData;

public class DavisApsCorrection {
  final int[] pitchblack;
  int count = -1;

  public DavisApsCorrection(String serial) {
    pitchblack = Primitives.toArrayInt(Flatten.of(Transpose.of( //
        ResourceData.of("/davis/" + serial + "/pitchblack.png").multiply(RealScalar.of(4)))));
  }

  public int next() {
    return pitchblack[++count];
  }

  public void reset() {
    count = -1;
  }
}
