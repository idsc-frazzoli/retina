// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class RimoRateRow extends GokartLogImageRow implements RimoGetListener {
  private static final Clip CLIP = Clips.positive(Quantity.of(75, SI.PER_SECOND));
  // ---
  private final int index;
  private Scalar scalar = RealScalar.ZERO;

  public RimoRateRow(int index) {
    this.index = index;
  }

  @Override // from RimoGetListener
  public void getEvent(RimoGetEvent rimoGetEvent) {
    scalar = CLIP.rescale(rimoGetEvent.getAngularRate_Y_pair().Get(index).abs());
  }

  @Override // from GokartLogImageRow
  public Scalar getScalar() {
    return scalar;
  }

  @Override // from GokartLogImageRow
  public ColorDataGradient getColorDataGradient() {
    return ColorDataGradients.CLASSIC;
  }

  @Override // from GokartLogImageRow
  public String getName() {
    return "rimo rate " + index;
  }
}
