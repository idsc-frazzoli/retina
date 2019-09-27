// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrameListener;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class Vmu931RateRow extends GokartLogImageRow implements Vmu931ImuFrameListener {
  private static final Clip CLIP = Clips.positive(1000);
  // ---
  private Scalar scalar = RealScalar.ZERO;

  @Override // from Vmu931ImuFrameListener
  public void vmu931ImuFrame(Vmu931ImuFrame vmu931ImuFrame) {
    scalar = scalar.add(RealScalar.ONE);
  }

  @Override // from GokartLogImageRow
  public Scalar getScalar() {
    Scalar value = scalar;
    scalar = RealScalar.ZERO;
    return CLIP.rescale(value);
  }

  @Override // from GokartLogImageRow
  public ColorDataGradient getColorDataGradient() {
    return ColorDataGradients.STARRYNIGHT;
  }

  @Override // from GokartLogImageRow
  public String getName() {
    return "vmu931 rate";
  }
}
