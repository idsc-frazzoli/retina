// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class LinmotTemperatureRow extends GokartLogImageRow implements LinmotGetListener {
  private static final Clip CLIP = Clips.interval( //
      Quantity.of(040, NonSI.DEGREE_CELSIUS), //
      Quantity.of(100, NonSI.DEGREE_CELSIUS));
  private Scalar scalar = RealScalar.ZERO;

  @Override // from LinmotGetListener
  public void getEvent(LinmotGetEvent linmotGetEvent) {
    scalar = CLIP.rescale(linmotGetEvent.getWindingTemperatureMax());
  }

  @Override // from GokartLogImageRow
  public Scalar getScalar() {
    return scalar;
  }

  @Override // from GokartLogImageRow
  public ColorDataGradient getColorDataGradient() {
    return ColorDataGradients.TEMPERATURE;
  }

  @Override // from GokartLogImageRow
  public String getName() {
    return "linmot temperature";
  }
}
