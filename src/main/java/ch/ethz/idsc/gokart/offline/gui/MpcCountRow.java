// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import ch.ethz.idsc.gokart.core.mpc.ControlAndPredictionSteps;
import ch.ethz.idsc.gokart.core.mpc.MPCControlUpdateListener;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class MpcCountRow extends GokartLogImageRow implements MPCControlUpdateListener {
  private static final Clip CLIP = Clips.positive(15);
  // ---
  private Scalar scalar = RealScalar.ZERO;

  @Override // from MPCControlUpdateListener
  public void getControlAndPredictionSteps(ControlAndPredictionSteps controlAndPredictionSteps) {
    scalar = scalar.add(RealScalar.ONE);
  }

  @Override // from GokartLogImageRow
  public Scalar getScalar() {
    Scalar value = CLIP.rescale(scalar);
    scalar = RealScalar.ZERO;
    return value;
  }

  @Override // from GokartLogImageRow
  public ColorDataGradient getColorDataGradient() {
    return ColorDataGradients.SUNSET;
  }

  @Override // from GokartLogImageRow
  public String getName() {
    return "mpc count";
  }
}
