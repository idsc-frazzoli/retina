// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.alg.Flatten;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.io.ResourceData;

/** correction of aps signal using an image that was taken in pitchblack mode
 * 
 * method is considered a simple approximation to the reset read
 * 
 * however, for the davis camera the constant baseline is not sufficiently accurate */
public class ConstantDavisApsCorrection extends DavisApsCorrection {
  public ConstantDavisApsCorrection(String serial) {
    super(Primitives.toArrayInt(Flatten.of(Transpose.of( //
        ResourceData.of("/davis/" + serial + "/pitchblack.png").multiply(RealScalar.of(4))))));
  }
}
