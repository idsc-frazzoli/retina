// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import ch.ethz.idsc.tensor.Integers;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class DvsCountRow extends ClipLogImageRow {
  private static final Clip CLIP = Clips.positive(800_000);
  // ---
  private int total;

  public void increment(int count) {
    total += Integers.requirePositiveOrZero(count);
  }

  @Override // from GokartLogImageRow
  public Scalar getScalar() {
    Scalar value = CLIP.rescale(RealScalar.of(total));
    total = 0;
    return value;
  }

  @Override // from GokartLogImageRow
  public ColorDataGradient getColorDataGradient() {
    return ColorDataGradients.COPPER;
  }

  @Override // from GokartLogImageRow
  public String getName() {
    return "dvs events";
  }

  @Override
  public Clip clip() {
    return CLIP;
  }
}
