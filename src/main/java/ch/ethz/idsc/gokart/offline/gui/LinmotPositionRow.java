// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.LinearColorDataGradient;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class LinmotPositionRow extends ClipLogImageRow implements LinmotGetListener {
  private static final ColorDataGradient COLOR_DATA_GRADIENT = //
      LinearColorDataGradient.of(Reverse.of(ResourceData.of("/colorscheme/rose.csv")));
  private static final Clip CLIP = Clips.interval(Quantity.of(-0.05, SI.METER), Quantity.of(0.0, SI.METER));
  // ---
  private Scalar scalar = RealScalar.ZERO;

  @Override // from LinmotGetListener
  public void getEvent(LinmotGetEvent linmotGetEvent) {
    scalar = CLIP.rescale(linmotGetEvent.getActualPosition());
  }

  @Override // from GokartLogImageRow
  public Scalar getScalar() {
    return scalar;
  }

  @Override // from GokartLogImageRow
  public ColorDataGradient getColorDataGradient() {
    return COLOR_DATA_GRADIENT;
  }

  @Override // from GokartLogImageRow
  public String getName() {
    return "linmot position";
  }

  @Override
  public Clip clip() {
    return CLIP;
  }
}
