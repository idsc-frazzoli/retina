// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import ch.ethz.idsc.gokart.core.pure.ClothoidPlan;
import ch.ethz.idsc.gokart.core.pure.ClothoidPlanListener;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

public class ClothoidPlanRow extends GokartLogImageRow implements ClothoidPlanListener {
  private static final Clip CLIP = Clips.positive(10);
  // ---
  private Scalar scalar = RealScalar.ZERO;

  @Override // from ClothoidPlanListener
  public void planReceived(ClothoidPlan clothoidPlan) {
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
    return "plans count";
  }
}
