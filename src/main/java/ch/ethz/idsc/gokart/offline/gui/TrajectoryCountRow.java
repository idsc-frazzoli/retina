// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class TrajectoryCountRow extends GokartLogImageRow {
  private static final Clip CLIP = Clips.positive(2);
  // ---
  private Scalar scalar = RealScalar.ZERO;

  public void increment() {
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
    return ColorDataGradients.BONE;
  }

  @Override // from GokartLogImageRow
  public String getName() {
    return "trajectory count";
  }
}
